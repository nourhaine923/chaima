package com.example.beautymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FactureAdapter extends RecyclerView.Adapter<FactureAdapter.FactureViewHolder> {

    private List<Facture> factureList;
    private OnFactureClickListener clickListener;
    private OnFactureLongClickListener longClickListener;

    public interface OnFactureClickListener {
        void onFactureClick(Facture facture, int position);
    }

    public interface OnFactureLongClickListener {
        void onFactureLongClick(Facture facture, int position);
    }

    public FactureAdapter() {
        this.factureList = new ArrayList<>();
    }

    public FactureAdapter(OnFactureClickListener clickListener) {
        this();
        this.clickListener = clickListener;
    }

    public void setOnFactureClickListener(OnFactureClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnFactureLongClickListener(OnFactureLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public FactureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facture, parent, false);
        return new FactureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactureViewHolder holder, int position) {
        Facture facture = factureList.get(position);

        holder.tvNumeroFacture.setText(facture.getNumeroFacture());
        holder.tvClientNom.setText(facture.getClientNom());
        holder.tvMontant.setText(String.format("%.2f DT", facture.getMontant()));

        // Formater la date
        if (facture.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            holder.tvDate.setText(sdf.format(facture.getDate()));
        } else {
            holder.tvDate.setText("--/--/----");
        }

        // Colorier selon le statut
        String statut = facture.getStatut();
        holder.tvStatut.setText(statut);

        int statutColor = 0xFF666666; // Gris par défaut
        if (statut != null) {
            switch (statut) {
                case "Payée":
                    statutColor = 0xFF4CAF50; // Vert
                    break;
                case "En attente":
                    statutColor = 0xFFFF9800; // Orange
                    break;
                case "En retard":
                    statutColor = 0xFFF44336; // Rouge
                    break;
            }
        }
        holder.tvStatut.setTextColor(statutColor);

        // Clic simple
        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFactureClick(facture, position);
            }
        });

        // Clic long
        holder.cardView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onFactureLongClick(facture, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return factureList.size();
    }

    public void setFactures(List<Facture> factures) {
        this.factureList = factures;
        notifyDataSetChanged();
    }

    public void addFacture(Facture facture) {
        factureList.add(0, facture);
        notifyItemInserted(0);
    }

    public void updateFacture(int position, Facture facture) {
        factureList.set(position, facture);
        notifyItemChanged(position);
    }

    public void removeFacture(int position) {
        factureList.remove(position);
        notifyItemRemoved(position);
    }

    static class FactureViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvNumeroFacture;
        TextView tvClientNom;
        TextView tvMontant;
        TextView tvDate;
        TextView tvStatut;

        public FactureViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardFacture);
            tvNumeroFacture = itemView.findViewById(R.id.tvNumeroFacture);
            tvClientNom = itemView.findViewById(R.id.tvClientNom);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatut = itemView.findViewById(R.id.tvStatut);
        }
    }
}