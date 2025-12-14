package com.example.beautymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RendezVousAdapter extends RecyclerView.Adapter<RendezVousAdapter.RendezVousViewHolder> {

    private List<RendezVous> rendezVousList;
    private OnRendezVousActionListener listener;

    public interface OnRendezVousActionListener {
        void onValiderClick(RendezVous rendezVous, int position);
        void onAnnulerClick(RendezVous rendezVous, int position);
    }

    public RendezVousAdapter(OnRendezVousActionListener listener) {
        this.rendezVousList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RendezVousViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rendezvous, parent, false);
        return new RendezVousViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RendezVousViewHolder holder, int position) {
        RendezVous rendezVous = rendezVousList.get(position);
        holder.bind(rendezVous, position);
    }

    @Override
    public int getItemCount() {
        return rendezVousList.size();
    }

    public void addRendezVous(RendezVous rendezVous) {
        rendezVousList.add(rendezVous);
        notifyItemInserted(rendezVousList.size() - 1);
    }

    public void clearRendezVous() {
        rendezVousList.clear();
        notifyDataSetChanged();
    }
    public void setRendezVousList(List<RendezVous> list) {
        this.rendezVousList = list;
        notifyDataSetChanged();
    }


    class RendezVousViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeure;
        TextView tvClientNom;
        TextView tvPrestationNom;
        TextView tvStatut;
        Button btnValider;
        Button btnAnnuler;

        public RendezVousViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeure = itemView.findViewById(R.id.tvHeure);
            tvClientNom = itemView.findViewById(R.id.tvClientNom);
            tvPrestationNom = itemView.findViewById(R.id.tvPrestationNom);
            tvStatut = itemView.findViewById(R.id.tvStatut);
            btnValider = itemView.findViewById(R.id.btnValider);
            btnAnnuler = itemView.findViewById(R.id.btnAnnuler);
        }

        public void bind(RendezVous rendezVous, int position) {
            tvHeure.setText(rendezVous.getHeure());
            tvClientNom.setText(rendezVous.getClientNom());
            tvPrestationNom.setText(rendezVous.getPrestationNom());
            tvStatut.setText(rendezVous.getStatut());

            // Gestion de la visibilité et de la couleur du statut
            int statutColor;
            switch (rendezVous.getStatut()) {
                case "Validé":
                    statutColor = Color.parseColor("#4CAF50"); // Vert
                    btnValider.setVisibility(View.GONE);
                    btnAnnuler.setVisibility(View.VISIBLE);
                    break;
                case "Annulé":
                    statutColor = Color.parseColor("#F44336"); // Rouge
                    btnValider.setVisibility(View.GONE);
                    btnAnnuler.setVisibility(View.GONE);
                    break;
                case "En attente":
                default:
                    statutColor = Color.parseColor("#FF9800"); // Orange
                    btnValider.setVisibility(View.VISIBLE);
                    btnAnnuler.setVisibility(View.VISIBLE);
                    break;
            }
            tvStatut.setBackgroundColor(statutColor);

            // Listeners pour les boutons d'action
            btnValider.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onValiderClick(rendezVous, position);
                }
            });

            btnAnnuler.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnnulerClick(rendezVous, position);
                }
            });
        }
    }
}
