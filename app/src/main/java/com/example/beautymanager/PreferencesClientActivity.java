package com.example.beautymanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesClientActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClienteAdapter adapter;

    private final List<Cliente> clientList = new ArrayList<>();
    private final List<Cliente> filteredClientList = new ArrayList<>();

    private FirebaseFirestore db;

    private ProgressBar progressBar;
    private TextView tvTotalClients, tvFilteredCount;
    private SearchView searchView;
    private FloatingActionButton fabAddClient;
    private View btnBack, btnFilter, emptyState, btnAddFirstClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_client);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();
        setupListeners();
        setupSearchView();
        loadClients();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        btnAddFirstClient = findViewById(R.id.btnAddFirstClient);

        tvTotalClients = findViewById(R.id.tvTotalClients);
        tvFilteredCount = findViewById(R.id.tvFilteredCount);

        searchView = findViewById(R.id.searchView);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        fabAddClient = findViewById(R.id.fabAddClient);
    }

    private void setupRecyclerView() {
        adapter = new ClienteAdapter(filteredClientList, new ClienteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(
                        PreferencesClientActivity.this,
                        filteredClientList.get(position).getNom(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onViewProfileClick(int position) {
                Toast.makeText(
                        PreferencesClientActivity.this,
                        "Profil: " + filteredClientList.get(position).getNom(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onStatusChanged(int position, boolean isActive) {
                Cliente c = filteredClientList.get(position);
                if (c.getId() != null) {
                    updateClientStatus(c.getId(), isActive, position);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnFilter.setOnClickListener(v ->
                Toast.makeText(this, "Filtre à venir", Toast.LENGTH_SHORT).show()
        );
        fabAddClient.setOnClickListener(v -> showAddClientDialog());
        btnAddFirstClient.setOnClickListener(v -> showAddClientDialog());
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterClients(newText);
                return true;
            }
        });
    }

    // ================= LOAD CLIENTS =================

    private void loadClients() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("clients")
                .get()
                .addOnSuccessListener(snapshot -> {
                    progressBar.setVisibility(View.GONE);
                    clientList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Cliente c = doc.toObject(Cliente.class);
                        c.setId(doc.getId());
                        clientList.add(c);
                    }

                    showAllClients();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur chargement clients", Toast.LENGTH_SHORT).show()
                );
    }

    // ✅ SEULE fonction d’affichage
    private void showAllClients() {
        filteredClientList.clear();
        filteredClientList.addAll(clientList);

        adapter.updateData(filteredClientList);

        tvTotalClients.setText(String.valueOf(clientList.size()));
        tvFilteredCount.setText(String.valueOf(filteredClientList.size()));

        if (clientList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    // ================= SEARCH =================

    private void filterClients(String query) {
        filteredClientList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredClientList.addAll(clientList);
        } else {
            String q = query.toLowerCase();

            for (Cliente c : clientList) {
                if ((c.getNom() != null && c.getNom().toLowerCase().contains(q)) ||
                        (c.getTelephone() != null && c.getTelephone().contains(query))) {
                    filteredClientList.add(c);
                }
            }
        }

        adapter.updateData(filteredClientList);
        tvFilteredCount.setText(String.valueOf(filteredClientList.size()));
        // ⚠️ PAS de emptyState ici
    }

    // ================= UPDATE STATUS =================

    private void updateClientStatus(String id, boolean active, int position) {
        Map<String, Object> data = new HashMap<>();
        data.put("active", active);

        db.collection("clients").document(id)
                .update(data)
                .addOnSuccessListener(a ->
                        adapter.updateStatus(position, active)
                );
    }

    // ================= ADD CLIENT =================

    private void showAddClientDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_client, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ajouter un client")
                .setView(view)
                .setPositiveButton("Ajouter", null)
                .setNegativeButton("Annuler", null)
                .create();

        dialog.setOnShowListener(d -> {
            TextInputEditText etNom = view.findViewById(R.id.editNom);
            TextInputEditText etPrenom = view.findViewById(R.id.editPrenom);
            TextInputEditText etTelephone = view.findViewById(R.id.editTelephone);
            SwitchCompat switchActif = view.findViewById(R.id.switchActif);

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String nom = etNom.getText().toString().trim();
                        String prenom = etPrenom.getText().toString().trim();
                        String tel = etTelephone.getText().toString().trim();

                        if (nom.isEmpty() || tel.isEmpty()) {
                            Toast.makeText(this, "Nom et téléphone requis", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Cliente cliente = new Cliente(
                                prenom.isEmpty() ? nom : nom + " " + prenom,
                                tel,
                                "",
                                "",
                                "",
                                ""
                        );
                        cliente.setActive(switchActif.isChecked());

                        db.collection("clients")
                                .add(cliente)
                                .addOnSuccessListener(doc -> {
                                    cliente.setId(doc.getId());
                                    clientList.add(cliente);
                                    showAllClients();
                                    dialog.dismiss();
                                });
                    });
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
