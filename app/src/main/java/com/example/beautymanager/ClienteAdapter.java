package com.example.beautymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onViewProfileClick(int position);
        void onStatusChanged(int position, boolean isActive);
    }

    private List<Cliente> clientList;
    private OnItemClickListener listener;

    public ClienteAdapter(List<Cliente> clientList, OnItemClickListener listener) {
        this.clientList = clientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cliente cliente = clientList.get(position);

        // Initialisation des vues
        if (holder.tvName != null) {
            holder.tvName.setText(cliente.getNom());
        }

        if (holder.tvPhone != null) {
            holder.tvPhone.setText(cliente.getTelephone());
        }

        if (holder.tvEmail != null) {
            holder.tvEmail.setText(cliente.getEmail());
        }

        if (holder.tvInitials != null) {
            // Générer les initiales à partir du nom
            String initials = getInitials(cliente.getNom());
            holder.tvInitials.setText(initials);
        }

        if (holder.tvHairType != null) {
            holder.tvHairType.setText("Cheveux: " + cliente.getTypeCheveux());
        }

        if (holder.tvHairLength != null) {
            holder.tvHairLength.setText("Longueur: " + cliente.getLongueurCheveux());
        }

        if (holder.tvFavoriteService != null) {
            holder.tvFavoriteService.setText("Service favori: " + cliente.getServiceFavori());
        }

        if (holder.switchStatus != null) {
            holder.switchStatus.setChecked(cliente.isActive());
        }

        // Gestion des clics
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

        // Vérifier si le bouton ViewProfile existe avant d'ajouter le listener
        if (holder.btnViewProfile != null) {
            holder.btnViewProfile.setOnClickListener(v -> {
                if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onViewProfileClick(holder.getAdapterPosition());
                }
            });
        }

        // Vérifier si le bouton Appointment existe avant d'ajouter le listener
        if (holder.btnAppointment != null) {
            holder.btnAppointment.setOnClickListener(v -> {
                // Logique pour prendre rendez-vous
                // Vous pouvez ajouter un listener ici si nécessaire
            });
        }

        // Vérifier si le switch de statut existe
        if (holder.switchStatus != null) {
            // Supprimer d'abord les anciens listeners pour éviter les boucles
            holder.switchStatus.setOnCheckedChangeListener(null);

            // Configurer l'état initial
            holder.switchStatus.setChecked(cliente.isActive());

            // Ajouter le nouveau listener
            holder.switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onStatusChanged(holder.getAdapterPosition(), isChecked);
                    }
                }
            });
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "??";
        }

        String[] parts = name.split(" ");
        StringBuilder initials = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
                if (initials.length() >= 2) {
                    break;
                }
            }
        }

        return initials.toString().toUpperCase();
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void updateData(List<Cliente> newList) {
        clientList.clear();
        clientList.addAll(newList);
        notifyDataSetChanged();
    }

    public void addItem(Cliente cliente) {
        clientList.add(cliente);
        notifyItemInserted(clientList.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < clientList.size()) {
            clientList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateStatus(int position, boolean isActive) {
        if (position >= 0 && position < clientList.size()) {
            clientList.get(position).setActive(isActive);
            notifyItemChanged(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPhone;
        TextView tvEmail;
        TextView tvInitials;
        TextView tvHairType;
        TextView tvHairLength;
        TextView tvFavoriteService;
        TextView btnViewProfile;
        TextView btnAppointment;
        SwitchCompat switchStatus;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialisation avec les IDs EXACTS de item_cliente.xml
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvHairType = itemView.findViewById(R.id.tvHairType);
            tvHairLength = itemView.findViewById(R.id.tvHairLength);
            tvFavoriteService = itemView.findViewById(R.id.tvFavoriteService);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
            btnAppointment = itemView.findViewById(R.id.btnAppointment);
            switchStatus = itemView.findViewById(R.id.switchStatus);
        }
    }
}