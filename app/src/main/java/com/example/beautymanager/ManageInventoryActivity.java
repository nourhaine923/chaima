package com.example.beautymanager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageInventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private EditText etProductName, etProductBrand, etProductPrice, etProductStock;
    private Button btnAdd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        // Initialiser Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerView);
        etProductName = findViewById(R.id.etProductName);
        etProductBrand = findViewById(R.id.etProductBrand);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductStock = findViewById(R.id.etProductStock);
        btnAdd = findViewById(R.id.btnAdd);

        // Configurer le RecyclerView
        productList = new ArrayList<>();
        adapter = new InventoryAdapter(productList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Configurer le bouton d'ajout
        btnAdd.setOnClickListener(v -> addProduct());

        // Charger les produits
        loadProducts();
    }

    private void loadProducts() {
        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ManageInventoryActivity.this,
                                "Erreur de chargement: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProduct() {
        String name = etProductName.getText().toString().trim();
        String brand = etProductBrand.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String stockStr = etProductStock.getText().toString().trim();

        if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("brand", brand);
            product.put("price", price);
            product.put("stock", stock);

            db.collection("inventory")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show();
                        clearForm();
                        loadProducts();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this,
                            "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix ou stock invalide", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etProductName.setText("");
        etProductBrand.setText("");
        etProductPrice.setText("");
        etProductStock.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadProducts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Product product = productList.get(position);
        showProductDetailsDialog(product);
    }

    @Override
    public void onItemLongClick(int position) {
        Product product = productList.get(position);
        deleteProduct(product);
    }

    private void showProductDetailsDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Détails du Produit")
                .setMessage("Nom: " + product.getName() + "\n" +
                        "Marque: " + product.getBrand() + "\n" +
                        "Prix: " + product.getPrice() + " DT\n" +
                        "Stock: " + product.getStock())
                .setPositiveButton("OK", null)
                .show();
    }

    private void deleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le produit")
                .setMessage("Voulez-vous vraiment supprimer " + product.getName() + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("inventory").document(product.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ManageInventoryActivity.this,
                                            "Produit supprimé", Toast.LENGTH_SHORT).show();
                                    loadProducts();
                                })
                                .addOnFailureListener(e -> Toast.makeText(ManageInventoryActivity.this,
                                        "Erreur de suppression", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }
}