package com.example.beautymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// IMPORTANT: VÉRIFIEZ LES IMPORTS - NE DOIT PAS AVOIR D'IMPORT STATIQUE DE R
// SUPPRIMEZ SI VOUS AVEZ : import static android.os.Build.VERSION_CODES.R;
// SUPPRIMEZ SI VOUS AVEZ : import static com.example.beautymanager.R;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private List<Product> productList;
    private OnItemClickListener listener;
    private NumberFormat currencyFormat;
    private Locale locale;

    public InventoryAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
        this.locale = Locale.getDefault();
        this.currencyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // CORRECTION : Vérifiez que ces IDs existent dans votre item_inventory.xml
        holder.productName.setText(product.getName());

        // CORRECTION : Utilisez String.format pour éviter les warnings
        if (holder.productBrand != null) {
            holder.productBrand.setText(String.format(locale, "Marque: %s", product.getBrand()));
        }

        if (holder.productPrice != null) {
            holder.productPrice.setText(String.format(locale, "Prix: %.2f DT", product.getPrice()));
        }

        if (holder.productStock != null) {
            holder.productStock.setText(String.format(locale, "Stock: %d", product.getStock()));

            // CORRECTION : Vérifiez que ces couleurs existent dans colors.xml
            int stockColor;
            if (product.getStock() < 5) {
                stockColor = ContextCompat.getColor(holder.itemView.getContext(),
                        getResourceId(holder.itemView.getContext(), "stock_low", "color"));
            } else if (product.getStock() < 15) {
                stockColor = ContextCompat.getColor(holder.itemView.getContext(),
                        getResourceId(holder.itemView.getContext(), "stock_medium", "color"));
            } else {
                stockColor = ContextCompat.getColor(holder.itemView.getContext(),
                        getResourceId(holder.itemView.getContext(), "stock_high", "color"));
            }
            holder.productStock.setTextColor(stockColor);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    public void addItem(Product product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Méthode utilitaire pour obtenir l'ID d'une ressource
    private int getResourceId(android.content.Context context, String resourceName, String resourceType) {
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productBrand;
        TextView productPrice;
        TextView productStock;

        ViewHolder(View itemView) {
            super(itemView);
            // CORRECTION : Utilisez les IDs EXACTEMENT comme dans votre item_inventory.xml
            // Si vos IDs sont différents, ajustez-les ici
            productName = itemView.findViewById(R.id.productName); // ou R.id.tvProductName
            productBrand = itemView.findViewById(R.id.productBrand); // ou R.id.tvProductBrand
            productPrice = itemView.findViewById(R.id.productPrice); // ou R.id.tvProductPrice
            productStock = itemView.findViewById(R.id.stockLabel); // ou R.id.tvProductStock
        }
    }
}