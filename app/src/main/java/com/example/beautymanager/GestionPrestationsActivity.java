package com.example.beautymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GestionPrestationsActivity extends AppCompatActivity {

    private RecyclerView rvPrestations;
    private LinearLayout layoutEmptyState;
    private TextView tvTotalServices, tvAvgPrice, tvAvgDuration, tvCategories;
    private FloatingActionButton fabAddService;
    private EditText etSearch;
    private CardView chipAll, chipCoupe, chipColor, chipSoin;
    private TextView tvViewAll;

    private PrestationAdapter adapter;
    private FirebaseFirestore db;
    private List<Prestation> prestationList;
    private List<Prestation> filteredList;
    private String currentCategory = "Tous";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_prestations);

        // Initialisation Firebase
        db = FirebaseFirestore.getInstance();
        prestationList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialisation du ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Chargement...");
        progressDialog.setCancelable(false);

        // Initialisation des vues
        initializeViews();
        setupRecyclerView();
        setupFilterChips();
        setupSearch();
        setupClickListeners();

        // Charger les prestations depuis Firebase
        loadPrestationsFromFirebase();
    }

    private void initializeViews() {
        rvPrestations = findViewById(R.id.rvPrestations);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvTotalServices = findViewById(R.id.tvTotalServices);
        tvAvgPrice = findViewById(R.id.tvAvgPrice);
        tvAvgDuration = findViewById(R.id.tvAvgDuration);
        tvCategories = findViewById(R.id.tvCategories);
        fabAddService = findViewById(R.id.fabAddService);
        etSearch = findViewById(R.id.etSearch);
        tvViewAll = findViewById(R.id.tvViewAll);

        chipAll = findViewById(R.id.chipAll);
        chipCoupe = findViewById(R.id.chipCoupe);
        chipColor = findViewById(R.id.chipColor);
        chipSoin = findViewById(R.id.chipSoin);
    }

    private void setupRecyclerView() {
        adapter = new PrestationAdapter(prestationList);

        adapter.setOnPrestationClickListener(new PrestationAdapter.OnPrestationClickListener() {
            @Override
            public void onPrestationClick(Prestation prestation) {
                showPrestationDetails(prestation);
            }

            @Override
            public void onPrestationEdit(Prestation prestation) {
                showEditPrestationDialog(prestation);
            }

            @Override
            public void onPrestationDelete(Prestation prestation) {
                showDeleteConfirmation(prestation);
            }
        });

        rvPrestations.setLayoutManager(new LinearLayoutManager(this));
        rvPrestations.setAdapter(adapter);
    }

    private void setupFilterChips() {
        chipAll.setOnClickListener(v -> {
            currentCategory = "Tous";
            updateChipSelection(chipAll);
            filterPrestationsByCategory("Tous");
        });

        chipCoupe.setOnClickListener(v -> {
            currentCategory = "Coupe";
            updateChipSelection(chipCoupe);
            filterPrestationsByCategory("Coupe");
        });

        chipColor.setOnClickListener(v -> {
            currentCategory = "Coloration";
            updateChipSelection(chipColor);
            filterPrestationsByCategory("Coloration");
        });

        chipSoin.setOnClickListener(v -> {
            currentCategory = "Soin";
            updateChipSelection(chipSoin);
            filterPrestationsByCategory("Soin");
        });

        updateChipSelection(chipAll);
    }

    private void updateChipSelection(CardView selectedChip) {
        resetChipStyle(chipAll);
        resetChipStyle(chipCoupe);
        resetChipStyle(chipColor);
        resetChipStyle(chipSoin);

        selectedChip.setCardBackgroundColor(getResources().getColor(R.color.violet_moyen));
        TextView selectedText = (TextView) selectedChip.getChildAt(0);
        selectedText.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetChipStyle(CardView chip) {
        chip.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        TextView text = (TextView) chip.getChildAt(0);
        text.setTextColor(getResources().getColor(R.color.violet_moyen));
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPrestations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        fabAddService.setOnClickListener(v -> showAddPrestationDialog());

        tvViewAll.setOnClickListener(v -> {
            filterPrestationsByCategory("Tous");
            updateChipSelection(chipAll);
            etSearch.setText("");
        });
    }

    private void loadPrestationsFromFirebase() {
        showLoading(true);

        db.collection("prestations")
                .orderBy("nom")
                .get()
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful() && task.getResult() != null) {
                        prestationList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            // Créer la prestation depuis les données Firebase
                            Prestation prestation = new Prestation();
                            prestation.setId(document.getId());
                            prestation.setNom((String) data.get("nom"));
                            prestation.setCategorie((String) data.get("categorie"));
                            prestation.setPrix(((Number) data.get("prix")).doubleValue());
                            prestation.setDuree((String) data.get("duree"));
                            prestation.setDescription((String) data.get("description"));

                            prestationList.add(prestation);
                        }

                        filterPrestationsByCategory(currentCategory);
                        updateStatistics();
                        updateUIVisibility();

                    } else {
                        showError("Erreur de chargement: " +
                                (task.getException() != null ?
                                        task.getException().getMessage() : "Erreur inconnue"));
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Erreur de connexion: " + e.getMessage());
                });
    }

    private void filterPrestationsByCategory(String category) {
        filteredList.clear();

        if (category.equalsIgnoreCase("Tous")) {
            filteredList.addAll(prestationList);
        } else {
            for (Prestation p : prestationList) {
                if (p.getCategorie() != null &&
                        p.getCategorie().equalsIgnoreCase(category)) {
                    filteredList.add(p);
                }
            }
        }

        adapter.updateData(filteredList);
        updateUIVisibility();
    }


    private void searchPrestations(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filterPrestationsByCategory(currentCategory);
            return;
        }

        String searchQuery = query.toLowerCase();

        for (Prestation p : prestationList) {
            boolean matchesCategory =
                    currentCategory.equals("Tous") ||
                            (p.getCategorie() != null &&
                                    p.getCategorie().equalsIgnoreCase(currentCategory));

            boolean matchesSearch =
                    (p.getNom() != null &&
                            p.getNom().toLowerCase().contains(searchQuery)) ||
                            (p.getDescription() != null &&
                                    p.getDescription().toLowerCase().contains(searchQuery));

            if (matchesCategory && matchesSearch) {
                filteredList.add(p);
            }
        }

        adapter.updateData(filteredList);
        updateUIVisibility();
    }


    private void updateStatistics() {
        if (prestationList.isEmpty()) {
            tvTotalServices.setText("0");
            tvAvgPrice.setText("0 DT");
            tvAvgDuration.setText("0h");
            tvCategories.setText("0");
            return;
        }

        tvTotalServices.setText(String.valueOf(prestationList.size()));

        double totalPrice = 0;
        for (Prestation p : prestationList) {
            totalPrice += p.getPrix();
        }
        double avgPrice = totalPrice / prestationList.size();
        tvAvgPrice.setText(String.format(Locale.FRANCE, "%.0f DT", avgPrice));

        int totalMinutes = 0;
        int countWithDuration = 0;

        for (Prestation p : prestationList) {
            if (p.getDuree() != null) {
                int minutes = convertDurationToMinutes(p.getDuree());
                if (minutes > 0) {
                    totalMinutes += minutes;
                    countWithDuration++;
                }
            }
        }

        if (countWithDuration > 0) {
            int avgMinutes = totalMinutes / countWithDuration;
            if (avgMinutes >= 60) {
                int hours = avgMinutes / 60;
                int mins = avgMinutes % 60;
                tvAvgDuration.setText(String.format("%dh%02d", hours, mins));
            } else {
                tvAvgDuration.setText(String.format("%dmin", avgMinutes));
            }
        } else {
            tvAvgDuration.setText("--");
        }

        HashSet<String> categories = new HashSet<>();
        for (Prestation p : prestationList) {
            if (p.getCategorie() != null) {
                categories.add(p.getCategorie());
            }
        }
        tvCategories.setText(String.valueOf(categories.size()));
    }

    private int convertDurationToMinutes(String duration) {
        try {
            if (duration == null) return 0;

            if (duration.contains("h")) {
                String[] parts = duration.split("h");
                int hours = Integer.parseInt(parts[0].trim());
                int minutes = 0;
                if (parts.length > 1 && parts[1].contains("min")) {
                    minutes = Integer.parseInt(parts[1].replace("min", "").trim());
                }
                return hours * 60 + minutes;
            } else if (duration.contains("min")) {
                return Integer.parseInt(duration.replace("min", "").trim());
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private void updateUIVisibility() {
        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvPrestations.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvPrestations.setVisibility(View.VISIBLE);
        }
    }

    private void showAddPrestationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un service");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_prestation, null);
        builder.setView(dialogView);

        EditText etNom = dialogView.findViewById(R.id.etNom);
        EditText etCategorie = dialogView.findViewById(R.id.etCategorie);
        EditText etPrix = dialogView.findViewById(R.id.etPrix);
        EditText etDuree = dialogView.findViewById(R.id.etDuree);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String nom = etNom.getText().toString().trim();
            String categorie = etCategorie.getText().toString().trim();
            String prixText = etPrix.getText().toString().trim();
            String duree = etDuree.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (nom.isEmpty() || categorie.isEmpty() || prixText.isEmpty() || duree.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double prix = Double.parseDouble(prixText);

                // Créer l'objet pour Firebase
                Map<String, Object> prestationData = new HashMap<>();
                prestationData.put("nom", nom);
                prestationData.put("categorie", categorie);
                prestationData.put("prix", prix);
                prestationData.put("duree", duree);
                prestationData.put("description", description);
                prestationData.put("createdAt", FieldValue.serverTimestamp());

                addPrestationToFirebase(prestationData);
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Le prix doit être un nombre valide",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showEditPrestationDialog(Prestation prestation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier " + prestation.getNom());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_prestation, null);
        builder.setView(dialogView);

        EditText etNom = dialogView.findViewById(R.id.etNom);
        EditText etCategorie = dialogView.findViewById(R.id.etCategorie);
        EditText etPrix = dialogView.findViewById(R.id.etPrix);
        EditText etDuree = dialogView.findViewById(R.id.etDuree);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        etNom.setText(prestation.getNom());
        etCategorie.setText(prestation.getCategorie());
        etPrix.setText(String.valueOf(prestation.getPrix()));
        etDuree.setText(prestation.getDuree());
        etDescription.setText(prestation.getDescription());

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String nom = etNom.getText().toString().trim();
            String categorie = etCategorie.getText().toString().trim();
            String prixText = etPrix.getText().toString().trim();
            String duree = etDuree.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (nom.isEmpty() || categorie.isEmpty() || prixText.isEmpty() || duree.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double prix = Double.parseDouble(prixText);

                // Mettre à jour l'objet
                prestation.setNom(nom);
                prestation.setCategorie(categorie);
                prestation.setPrix(prix);
                prestation.setDuree(duree);
                prestation.setDescription(description);

                updatePrestationInFirebase(prestation);
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Le prix doit être un nombre valide",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showPrestationDetails(Prestation prestation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Détails du service")
                .setMessage(
                        "✨ " + prestation.getNom() + "\n\n" +
                                "📁 Catégorie: " + prestation.getCategorie() + "\n" +
                                "💰 Prix: " + String.format("%.2f DT", prestation.getPrix()) + "\n" +
                                "⏱️ Durée: " + prestation.getDuree() + "\n" +
                                "📝 Description: " +
                                (prestation.getDescription() != null &&
                                        !prestation.getDescription().isEmpty() ?
                                        prestation.getDescription() : "Aucune description")
                )
                .setPositiveButton("Modifier", (dialog, which) -> {
                    showEditPrestationDialog(prestation);
                })
                .setNeutralButton("Supprimer", (dialog, which) -> {
                    showDeleteConfirmation(prestation);
                })
                .setNegativeButton("Fermer", null)
                .show();
    }

    private void showDeleteConfirmation(Prestation prestation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer \"" + prestation.getNom() + "\" ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    deletePrestationFromFirebase(prestation);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void addPrestationToFirebase(Map<String, Object> prestationData) {
        showLoading(true);

        db.collection("prestations")
                .add(prestationData)
                .addOnSuccessListener(documentReference -> {
                    showLoading(false);

                    // Créer l'objet Prestation localement
                    Prestation prestation = new Prestation();
                    prestation.setId(documentReference.getId());
                    prestation.setNom((String) prestationData.get("nom"));
                    prestation.setCategorie((String) prestationData.get("categorie"));
                    prestation.setPrix(((Number) prestationData.get("prix")).doubleValue());
                    prestation.setDuree((String) prestationData.get("duree"));
                    prestation.setDescription((String) prestationData.get("description"));

                    prestationList.add(prestation);
                    filterPrestationsByCategory(currentCategory);
                    updateStatistics();

                    Toast.makeText(this, "Service ajouté avec succès",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Erreur d'ajout: " + e.getMessage());
                });
    }

    private void updatePrestationInFirebase(Prestation prestation) {
        showLoading(true);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nom", prestation.getNom());
        updateData.put("categorie", prestation.getCategorie());
        updateData.put("prix", prestation.getPrix());
        updateData.put("duree", prestation.getDuree());
        updateData.put("description", prestation.getDescription());
        updateData.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("prestations").document(prestation.getId())
                .set(updateData)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);

                    // Mettre à jour la liste locale
                    for (int i = 0; i < prestationList.size(); i++) {
                        if (prestationList.get(i).getId().equals(prestation.getId())) {
                            prestationList.set(i, prestation);
                            break;
                        }
                    }

                    filterPrestationsByCategory(currentCategory);
                    Toast.makeText(this, "Service mis à jour",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Erreur de mise à jour: " + e.getMessage());
                });
    }

    private void deletePrestationFromFirebase(Prestation prestation) {
        showLoading(true);

        db.collection("prestations").document(prestation.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);

                    // Supprimer de la liste locale
                    for (int i = 0; i < prestationList.size(); i++) {
                        if (prestationList.get(i).getId().equals(prestation.getId())) {
                            prestationList.remove(i);
                            break;
                        }
                    }

                    filterPrestationsByCategory(currentCategory);
                    updateStatistics();
                    Toast.makeText(this, "Service supprimé",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Erreur de suppression: " + e.getMessage());
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrestationsFromFirebase();
    }
}