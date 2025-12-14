package com.example.beautymanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FacturationActivity extends AppCompatActivity implements
        FactureAdapter.OnFactureClickListener,
        FactureAdapter.OnFactureLongClickListener {

    private RecyclerView rvFactures;
    private LinearLayout layoutEmptyState;
    private TextView tvRevenueAmount;
    private TextView tvPaidCount;
    private TextView tvPendingCount;
    private TextView tvOverdueCount;
    private TextView tvViewAll;
    private FloatingActionButton fabNewInvoice;

    private FactureAdapter adapter;
    private FirebaseFirestore db;
    private List<Facture> factureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturation);

        // Initialiser Firebase Firestore
        db = FirebaseFirestore.getInstance();
        factureList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadFacturesFromFirebase();
    }

    private void initializeViews() {
        rvFactures = findViewById(R.id.rvFactures);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvRevenueAmount = findViewById(R.id.tvRevenueAmount);
        tvPaidCount = findViewById(R.id.tvPaidCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvOverdueCount = findViewById(R.id.tvOverdueCount);
        tvViewAll = findViewById(R.id.tvViewAll);
        fabNewInvoice = findViewById(R.id.fabNewInvoice);

        // Bouton pour ajouter une nouvelle facture
        fabNewInvoice.setOnClickListener(v -> showAddFactureDialog());

        // Bouton "Voir tout"
        tvViewAll.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Total des factures: " + factureList.size(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        adapter = new FactureAdapter(this);
        adapter.setOnFactureClickListener(this);
        adapter.setOnFactureLongClickListener(this);

        rvFactures.setLayoutManager(new LinearLayoutManager(this));
        rvFactures.setAdapter(adapter);
    }

    // ==================== FIREBASE OPERATIONS ====================

    /**
     * Charger les factures depuis Firebase Firestore
     */
    private void loadFacturesFromFirebase() {
        Log.d("FACTURATION", "Chargement des factures depuis Firebase...");

        db.collection("factures")
                .orderBy("date") // Trier par date (assurez-vous d'avoir un index si besoin)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        factureList.clear();
                        int count = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("FACTURATION", "Document " + (++count) + ": " + document.getId());

                            try {
                                // Convertir le document Firestore en objet Facture
                                Facture facture = document.toObject(Facture.class);
                                facture.setId(document.getId()); // Sauvegarder l'ID du document
                                factureList.add(facture);

                                Log.d("FACTURATION", "Facture ajoutée: " + facture.getNumeroFacture());
                            } catch (Exception e) {
                                Log.e("FACTURATION", "Erreur de conversion: " + e.getMessage());
                            }
                        }

                        Log.d("FACTURATION", "Total factures chargées: " + factureList.size());

                        // Trier par date (plus récent en premier)
                        factureList.sort((f1, f2) -> {
                            if (f1.getDate() == null || f2.getDate() == null) return 0;
                            return f2.getDate().compareTo(f1.getDate());
                        });

                        // Mettre à jour l'UI
                        adapter.setFactures(factureList);
                        updateStatistics();
                        updateUIVisibility();

                        Toast.makeText(this,
                                factureList.size() + " factures chargées",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        String errorMsg = "Erreur de chargement: " +
                                (task.getException() != null ?
                                        task.getException().getMessage() : "Erreur inconnue");

                        Log.e("FACTURATION", errorMsg);
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

                        // Mode démo en cas d'erreur
                        loadDemoData();
                    }
                });
    }

    /**
     * Ajouter une nouvelle facture à Firebase
     */
    private void addFactureToFirebase(Facture facture) {
        // Convertir l'objet Facture en Map pour Firestore
        Map<String, Object> factureData = new HashMap<>();
        factureData.put("numeroFacture", facture.getNumeroFacture());
        factureData.put("clientNom", facture.getClientNom());
        factureData.put("montant", facture.getMontant());
        factureData.put("date", facture.getDate() != null ? facture.getDate() : new Date());
        factureData.put("statut", facture.getStatut());

        Log.d("FACTURATION", "Ajout de facture: " + facture.getNumeroFacture());

        db.collection("factures")
                .add(factureData)
                .addOnSuccessListener(documentReference -> {
                    // Ajouter l'ID généré par Firebase à l'objet Facture
                    facture.setId(documentReference.getId());

                    // Ajouter à la liste locale et mettre à jour l'UI
                    factureList.add(0, facture);
                    adapter.addFacture(facture);
                    updateStatistics();
                    updateUIVisibility();

                    Toast.makeText(this, "Facture ajoutée avec succès", Toast.LENGTH_SHORT).show();
                    Log.d("FACTURATION", "Facture ajoutée avec ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erreur d'ajout: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e("FACTURATION", "Erreur d'ajout: " + e.getMessage());
                });
    }

    /**
     * Mettre à jour le statut d'une facture dans Firebase
     */
    private void updateFactureStatusInFirebase(Facture facture, String newStatus, int position) {
        if (facture.getId() == null || facture.getId().isEmpty()) {
            Toast.makeText(this, "Erreur: ID de facture manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("statut", newStatus);

        db.collection("factures").document(facture.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Mettre à jour l'objet local
                    facture.setStatut(newStatus);
                    adapter.updateFacture(position, facture);
                    updateStatistics();

                    Toast.makeText(this,
                            "Statut mis à jour: " + newStatus,
                            Toast.LENGTH_SHORT).show();
                    Log.d("FACTURATION", "Statut mis à jour pour: " + facture.getNumeroFacture());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erreur de mise à jour: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("FACTURATION", "Erreur mise à jour statut: " + e.getMessage());
                });
    }

    /**
     * Supprimer une facture de Firebase
     */
    private void deleteFactureFromFirebase(Facture facture, int position) {
        if (facture.getId() == null || facture.getId().isEmpty()) {
            // Pour les données de démo
            adapter.removeFacture(position);
            factureList.remove(position);
            updateStatistics();
            updateUIVisibility();
            Toast.makeText(this, "Facture supprimée", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("factures").document(facture.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Supprimer de la liste locale
                    adapter.removeFacture(position);
                    factureList.remove(position);
                    updateStatistics();
                    updateUIVisibility();

                    Toast.makeText(this, "Facture supprimée", Toast.LENGTH_SHORT).show();
                    Log.d("FACTURATION", "Facture supprimée: " + facture.getNumeroFacture());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erreur de suppression: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("FACTURATION", "Erreur suppression: " + e.getMessage());
                });
    }

    // ==================== UI UPDATES ====================

    /**
     * Mettre à jour les statistiques
     */
    private void updateStatistics() {
        double totalRevenue = 0;
        int paidCount = 0;
        int pendingCount = 0;
        int overdueCount = 0;

        for (Facture facture : factureList) {
            totalRevenue += facture.getMontant();

            String statut = facture.getStatut();
            if (statut != null) {
                switch (statut) {
                    case "Payée":
                        paidCount++;
                        break;
                    case "En attente":
                        pendingCount++;
                        break;
                    case "En retard":
                        overdueCount++;
                        break;
                }
            }
        }

        // Formater avec séparateur de milliers
        tvRevenueAmount.setText(String.format(Locale.FRANCE, "%,.0f DT", totalRevenue));
        tvPaidCount.setText(String.valueOf(paidCount));
        tvPendingCount.setText(String.valueOf(pendingCount));
        tvOverdueCount.setText(String.valueOf(overdueCount));
    }

    /**
     * Mettre à jour la visibilité des éléments UI
     */
    private void updateUIVisibility() {
        if (factureList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvFactures.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvFactures.setVisibility(View.VISIBLE);
        }
    }

    // ==================== DIALOGS ====================

    /**
     * Afficher le dialogue pour ajouter une facture
     */
    private void showAddFactureDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_facture, null);

        com.google.android.material.textfield.TextInputEditText editNumeroFacture =
                dialogView.findViewById(R.id.editNumeroFacture);
        com.google.android.material.textfield.TextInputEditText editClientNom =
                dialogView.findViewById(R.id.editClientNom);
        com.google.android.material.textfield.TextInputEditText editMontant =
                dialogView.findViewById(R.id.editMontant);
        com.google.android.material.textfield.TextInputEditText editStatut =
                dialogView.findViewById(R.id.editStatut);

        // Générer un numéro de facture automatique
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        String datePrefix = sdf.format(new Date());
        int nextNum = factureList.size() + 1;
        String numeroAuto = "FACT-" + datePrefix + "-" + String.format("%03d", nextNum);
        editNumeroFacture.setText(numeroAuto);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nouvelle Facture")
                .setView(dialogView)
                .setPositiveButton("Ajouter", null)
                .setNegativeButton("Annuler", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String numero = editNumeroFacture.getText().toString().trim();
                String client = editClientNom.getText().toString().trim();
                String montantStr = editMontant.getText().toString().trim();
                String statut = editStatut.getText().toString().trim();

                if (numero.isEmpty()) {
                    editNumeroFacture.setError("Numéro requis");
                    return;
                }
                if (client.isEmpty()) {
                    editClientNom.setError("Client requis");
                    return;
                }
                if (montantStr.isEmpty()) {
                    editMontant.setError("Montant requis");
                    return;
                }
                if (statut.isEmpty()) {
                    statut = "En attente";
                }

                try {
                    double montant = Double.parseDouble(montantStr);
                    if (montant <= 0) {
                        editMontant.setError("Montant doit être > 0");
                        return;
                    }

                    // Créer la nouvelle facture
                    Facture newFacture = new Facture(numero, client, montant, new Date(), statut);

                    // Ajouter à Firebase
                    addFactureToFirebase(newFacture);

                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    editMontant.setError("Montant invalide");
                }
            });
        });

        dialog.show();
    }

    /**
     * Afficher les détails d'une facture
     */
    private void showFactureDetails(Facture facture, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        String dateStr = facture.getDate() != null ?
                sdf.format(facture.getDate()) : "Date non définie";

        String details = "📋 " + facture.getNumeroFacture() + "\n\n" +
                "👤 Client: " + facture.getClientNom() + "\n" +
                "💰 Montant: " + String.format("%.2f DT", facture.getMontant()) + "\n" +
                "📅 Date: " + dateStr + "\n" +
                "✅ Statut: " + facture.getStatut();

        new AlertDialog.Builder(this)
                .setTitle("Détails de la facture")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .setNeutralButton("Actions", (dialog, which) -> {
                    showActionsMenu(facture, position);
                })
                .setNegativeButton("Supprimer", (dialog, which) -> {
                    deleteFactureConfirmation(facture, position);
                })
                .show();
    }

    /**
     * Menu d'actions pour une facture
     */
    private void showActionsMenu(Facture facture, int position) {
        String[] actions = {
                "✅ Marquer comme Payée",
                "⏳ Marquer comme En attente",
                "⚠️ Marquer comme En retard",
                "🗑️ Supprimer cette facture",
                "📊 Voir les statistiques"
        };

        new AlertDialog.Builder(this)
                .setTitle("Actions: " + facture.getNumeroFacture())
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Payée
                            updateFactureStatusInFirebase(facture, "Payée", position);
                            break;
                        case 1: // En attente
                            updateFactureStatusInFirebase(facture, "En attente", position);
                            break;
                        case 2: // En retard
                            updateFactureStatusInFirebase(facture, "En retard", position);
                            break;
                        case 3: // Supprimer
                            deleteFactureConfirmation(facture, position);
                            break;
                        case 4: // Statistiques
                            showStatisticsInfo();
                            break;
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Confirmation de suppression
     */
    private void deleteFactureConfirmation(Facture facture, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la facture")
                .setMessage("Êtes-vous sûr de vouloir supprimer \"" + facture.getNumeroFacture() + "\" ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    deleteFactureFromFirebase(facture, position);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Afficher les statistiques détaillées
     */
    private void showStatisticsInfo() {
        String statsInfo = "📊 Statistiques actuelles:\n\n" +
                "💰 Revenu total: " + tvRevenueAmount.getText() + "\n" +
                "✅ Factures payées: " + tvPaidCount.getText() + "\n" +
                "⏳ En attente: " + tvPendingCount.getText() + "\n" +
                "⚠️ En retard: " + tvOverdueCount.getText() + "\n" +
                "📈 Total factures: " + factureList.size();

        new AlertDialog.Builder(this)
                .setTitle("Statistiques")
                .setMessage(statsInfo)
                .setPositiveButton("OK", null)
                .show();
    }

    // ==================== DEMO DATA ====================

    /**
     * Charger des données de démo en cas d'erreur Firebase
     */
    private void loadDemoData() {
        factureList.clear();

        Calendar cal = Calendar.getInstance();

        // Ajouter des factures de démo
        factureList.add(new Facture("FACT-20251201-001", "Sophie Martin", 125.50, cal.getTime(), "Payée"));

        cal.add(Calendar.DAY_OF_MONTH, -1);
        factureList.add(new Facture("FACT-20251130-001", "Marie Laurent", 89.00, cal.getTime(), "En attente"));

        cal.add(Calendar.DAY_OF_MONTH, -2);
        factureList.add(new Facture("FACT-20251128-001", "Emma Bernard", 150.00, cal.getTime(), "En retard"));

        cal.add(Calendar.DAY_OF_MONTH, -3);
        factureList.add(new Facture("FACT-20251127-001", "Chloé Petit", 75.00, cal.getTime(), "Payée"));

        cal.add(Calendar.DAY_OF_MONTH, -4);
        factureList.add(new Facture("FACT-20251126-001", "Léa Moreau", 200.00, cal.getTime(), "En attente"));

        // Trier par date
        factureList.sort((f1, f2) -> f2.getDate().compareTo(f1.getDate()));

        adapter.setFactures(factureList);
        updateStatistics();
        updateUIVisibility();

        Toast.makeText(this, "Mode démo activé (5 factures)", Toast.LENGTH_SHORT).show();
    }

    // ==================== INTERFACE IMPLEMENTATIONS ====================

    @Override
    public void onFactureClick(Facture facture, int position) {
        showFactureDetails(facture, position);
    }

    @Override
    public void onFactureLongClick(Facture facture, int position) {
        showActionsMenu(facture, position);
    }

    // ==================== LIFECYCLE ====================

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données si nécessaire
        if (factureList.isEmpty()) {
            loadFacturesFromFirebase();
        }
    }
}