package com.example.beautymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Calendar;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRevenueAmount, tvAppointmentsToday, tvTotalProducts, tvTotalServices;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialisation Firebase
        db = FirebaseFirestore.getInstance();

        // Initialisation des vues
        initViews();

        // Configuration des listeners
        setupClickListeners();

        // Charger les données depuis Firebase
        loadDashboardData();
    }

    private void initViews() {
        tvRevenueAmount = findViewById(R.id.tvRevenueAmount);
        tvAppointmentsToday = findViewById(R.id.tvAppointmentsToday);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalServices = findViewById(R.id.tvTotalServices);

        // Cartes cliquables
        findViewById(R.id.cardRendezVous).setOnClickListener(this);
        findViewById(R.id.cardPrestations).setOnClickListener(this);
        findViewById(R.id.cardStock).setOnClickListener(this);
        findViewById(R.id.cardFacturation).setOnClickListener(this);
        findViewById(R.id.cardClients).setOnClickListener(this);
        findViewById(R.id.cardStats).setOnClickListener(this);
    }

    private void setupClickListeners() {
        // Vous pouvez ajouter d'autres listeners ici si nécessaire
    }

    private void loadDashboardData() {
        loadRevenueData();
        loadTodayAppointments();
        loadTotalProducts();
        loadTotalServices();
    }

    private void loadRevenueData() {
        // Récupérer le chiffre d'affaires du mois en cours
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Janvier = 0

        db.collection("invoices")
                .whereGreaterThanOrEqualTo("date", getFirstDayOfMonth())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRevenue = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Double amount = document.getDouble("total");
                        if (amount != null) {
                            totalRevenue += amount;
                        }
                    }
                    tvRevenueAmount.setText(String.format("%.2f DT", totalRevenue));
                })
                .addOnFailureListener(e -> {
                    tvRevenueAmount.setText("0 DT");
                    Toast.makeText(this, "Erreur chargement CA", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadTodayAppointments() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        db.collection("appointments")
                .whereGreaterThanOrEqualTo("dateTime", startOfDay)
                .whereLessThanOrEqualTo("dateTime", endOfDay)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long count = queryDocumentSnapshots.size();
                    tvAppointmentsToday.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> {
                    tvAppointmentsToday.setText("0");
                });
    }

    private void loadTotalProducts() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long count = queryDocumentSnapshots.size();
                    tvTotalProducts.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> {
                    tvTotalProducts.setText("0");
                });
    }

    private void loadTotalServices() {
        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long count = queryDocumentSnapshots.size();
                    tvTotalServices.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> {
                    tvTotalServices.setText("0");
                });
    }

    private Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();

        if (id == R.id.cardRendezVous) {
            intent = new Intent(this, GestionRendezVousActivity.class);
        } else if (id == R.id.cardPrestations) {
            intent = new Intent(this, GestionPrestationsActivity.class);
        } else if (id == R.id.cardStock) {
            intent = new Intent(this, ManageInventoryActivity.class);
        } else if (id == R.id.cardFacturation) {
            intent = new Intent(this, FacturationActivity.class);
        } else if (id == R.id.cardClients) {
            intent = new Intent(this, PreferencesClientActivity.class);
        } else if (id == R.id.cardStats) {
            intent = new Intent(this, StatisticsActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données quand on revient sur le dashboard
        loadDashboardData();
    }
}