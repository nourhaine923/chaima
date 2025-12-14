package com.example.beautymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GestionRendezVousActivity extends AppCompatActivity
        implements RendezVousAdapter.OnRendezVousActionListener {

    private RecyclerView rvRendezVous;
    private CalendarView calendarView;
    private FloatingActionButton fabAddRendezVous;
    private RendezVousAdapter adapter;
    private FirebaseFirestore db;
    private final List<RendezVous> rendezVousList = new ArrayList<>();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_rendezvous);

        db = FirebaseFirestore.getInstance();

        rvRendezVous = findViewById(R.id.rvRendezVous);
        calendarView = findViewById(R.id.calendarView);
        fabAddRendezVous = findViewById(R.id.fabAddRendezVous);

        adapter = new RendezVousAdapter(this);
        adapter.setRendezVousList(rendezVousList);

        rvRendezVous.setLayoutManager(new LinearLayoutManager(this));
        rvRendezVous.setAdapter(adapter);

        // Charger aujourd’hui
        loadRendezVousForDate(new Date());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            loadRendezVousForDate(cal.getTime());
        });

        // ✅ UN SEUL LISTENER
        fabAddRendezVous.setOnClickListener(v -> showAddRendezVousDialog());
    }

    // ================= LOAD =================

    private void loadRendezVousForDate(Date selectedDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(selectedDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);

        Calendar end = Calendar.getInstance();
        end.setTime(selectedDate);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        db.collection("rendezvous")
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThanOrEqualTo("date", end.getTime())
                .get()
                .addOnSuccessListener(snapshot -> {
                    rendezVousList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        RendezVous rv = doc.toObject(RendezVous.class);
                        rv.setId(doc.getId());
                        rendezVousList.add(rv);
                    }
                    adapter.setRendezVousList(rendezVousList);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erreur chargement rendez-vous",
                                Toast.LENGTH_SHORT).show()
                );
    }

    // ================= ADD =================

    private void showAddRendezVousDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_rendezvous, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ajouter un rendez-vous")
                .setView(view)
                .setPositiveButton("Ajouter", null)
                .setNegativeButton("Annuler", null)
                .create();

        dialog.setOnShowListener(d -> {
            EditText etClient = view.findViewById(R.id.etClient);
            EditText etPrestation = view.findViewById(R.id.etPrestation);
            EditText etHeure = view.findViewById(R.id.etHeure);

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String client = etClient.getText().toString().trim();
                        String prestation = etPrestation.getText().toString().trim();
                        String heure = etHeure.getText().toString().trim();

                        if (client.isEmpty() || prestation.isEmpty() || heure.isEmpty()) {
                            Toast.makeText(this,
                                    "Tous les champs sont obligatoires",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(calendarView.getDate());
                        Date date = cal.getTime();

                        RendezVous rv = new RendezVous(
                                date,
                                heure,
                                "En attente",
                                "",
                                client,
                                "",
                                prestation
                        );



                        addRendezVous(rv);
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

    private void addRendezVous(RendezVous rv) {
        db.collection("rendezvous")
                .add(rv)
                .addOnSuccessListener(doc -> {
                    rv.setId(doc.getId());
                    loadRendezVousForDate(rv.getDate());
                    Toast.makeText(this,
                            "Rendez-vous ajouté",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erreur ajout rendez-vous",
                                Toast.LENGTH_SHORT).show()
                );
    }

    // ================= ACTIONS =================

    @Override
    public void onValiderClick(RendezVous rv, int position) {
        updateStatus(rv, "Validé");
    }

    @Override
    public void onAnnulerClick(RendezVous rv, int position) {
        updateStatus(rv, "Annulé");
    }

    private void updateStatus(RendezVous rv, String statut) {
        db.collection("rendezvous").document(rv.getId())
                .update("statut", statut)
                .addOnSuccessListener(a -> {
                    rv.setStatut(statut);
                    adapter.notifyDataSetChanged();
                });
    }
}
