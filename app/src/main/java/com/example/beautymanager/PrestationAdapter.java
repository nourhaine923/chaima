package com.example.beautymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class PrestationAdapter extends RecyclerView.Adapter<PrestationAdapter.ViewHolder> {

    private List<Prestation> prestationList;
    private OnPrestationClickListener listener;

    public interface OnPrestationClickListener {
        void onPrestationClick(Prestation prestation);
        void onPrestationEdit(Prestation prestation);
        void onPrestationDelete(Prestation prestation);
    }

    public PrestationAdapter(List<Prestation> prestationList) {
        this.prestationList = prestationList;
    }

    public void setOnPrestationClickListener(OnPrestationClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Prestation> newPrestations) {
        prestationList.clear();
        prestationList.addAll(newPrestations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prestation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prestation prestation = prestationList.get(position);

        holder.tvNom.setText(prestation.getNom());
        holder.tvCategorie.setText(prestation.getCategorie());
        holder.tvPrix.setText(String.format("%.2f DT", prestation.getPrix()));
        holder.tvDuree.setText(prestation.getDuree());

        if (prestation.getDescription() != null && !prestation.getDescription().isEmpty()) {
            holder.tvDescription.setText(prestation.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Clic sur l'item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPrestationClick(prestation);
            }
        });

        // Bouton modifier
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPrestationEdit(prestation);
            }
        });

        // Bouton supprimer
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPrestationDelete(prestation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prestationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvCategorie, tvPrix, tvDuree, tvDescription;
        CardView cardCategory;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNom = itemView.findViewById(R.id.tvNom);
            tvCategorie = itemView.findViewById(R.id.tvCategorie);
            tvPrix = itemView.findViewById(R.id.tvPrix);
            tvDuree = itemView.findViewById(R.id.tvDuree);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}