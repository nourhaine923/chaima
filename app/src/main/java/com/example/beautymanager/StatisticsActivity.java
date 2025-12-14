package com.example.beautymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalRevenue, tvMonthlyRevenue, tvTotalClients, tvTotalAppointments;
    private TextView tvPopularService, tvRevenueGrowth, tvClientGrowth;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_simple);

        db = FirebaseFirestore.getInstance();
        initViews();
        loadStatistics();
    }

    private void initViews() {
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvMonthlyRevenue = findViewById(R.id.tvMonthlyRevenue);
        tvTotalClients = findViewById(R.id.tvTotalClients);
        tvTotalAppointments = findViewById(R.id.tvTotalAppointments);
        tvPopularService = findViewById(R.id.tvPopularService);
        tvRevenueGrowth = findViewById(R.id.tvRevenueGrowth);
        tvClientGrowth = findViewById(R.id.tvClientGrowth);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadStatistics() {
        progressBar.setVisibility(View.VISIBLE);

        loadRevenueStats();
        loadClientStats();
        loadRendezVousStats();
    }

    // ==================== REVENUE ====================

    private void loadRevenueStats() {
        db.collection("factures")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRevenue = 0;
                    double monthlyRevenue = 0;

                    Calendar now = Calendar.getInstance();
                    int currentMonth = now.get(Calendar.MONTH);
                    int currentYear = now.get(Calendar.YEAR);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double montant = doc.getDouble("montant");
                        Date date = doc.getDate("date");

                        if (montant != null) {
                            totalRevenue += montant;

                            if (date != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                if (cal.get(Calendar.MONTH) == currentMonth &&
                                        cal.get(Calendar.YEAR) == currentYear) {
                                    monthlyRevenue += montant;
                                }
                            }
                        }
                    }

                    NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);
                    tvTotalRevenue.setText(format.format(totalRevenue) + " DT");
                    tvMonthlyRevenue.setText(format.format(monthlyRevenue) + " DT");

                    double growth = totalRevenue > 0
                            ? (monthlyRevenue / totalRevenue) * 100
                            : 0;

                    tvRevenueGrowth.setText(String.format(Locale.FRANCE, "%.1f%%", growth));

                    progressBar.setVisibility(View.GONE);
                });
    }

    // ==================== CLIENTS ====================

    private void loadClientStats() {
        db.collection("clientes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalClients = queryDocumentSnapshots.size();
                    tvTotalClients.setText(String.valueOf(totalClients));

                    // Simple growth info (no createdAt required)
                    tvClientGrowth.setText("+" + totalClients + " total");
                });
    }

    // ==================== RENDEZ-VOUS ====================

    private void loadRendezVousStats() {
        db.collection("rendezvous")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalAppointments = queryDocumentSnapshots.size();
                    tvTotalAppointments.setText(String.valueOf(totalAppointments));

                    Map<String, Integer> prestationCount = new HashMap<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String prestation = doc.getString("prestationNom");
                        if (prestation != null && !prestation.isEmpty()) {
                            prestationCount.put(
                                    prestation,
                                    prestationCount.getOrDefault(prestation, 0) + 1
                            );
                        }
                    }

                    // Most popular service
                    String popular = "Aucun";
                    int max = 0;

                    for (Map.Entry<String, Integer> entry : prestationCount.entrySet()) {
                        if (entry.getValue() > max) {
                            max = entry.getValue();
                            popular = entry.getKey();
                        }
                    }

                    tvPopularService.setText(popular + (max > 0 ? " (" + max + "x)" : ""));
                });
    }
}
