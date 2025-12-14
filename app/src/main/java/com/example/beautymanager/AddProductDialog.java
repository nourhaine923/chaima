package com.example.beautymanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;

public class AddProductDialog extends Dialog {

    private EditText etProductName, etProductBrand, etProductPrice, etProductStock;
    private MaterialButton btnSave, btnCancel;
    private OnSaveClickListener saveClickListener;

    public interface OnSaveClickListener {
        void onSaveClick(View view);
    }

    public AddProductDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductBrand = findViewById(R.id.etProductBrand);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductStock = findViewById(R.id.etProductStock);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            if (saveClickListener != null) {
                saveClickListener.onSaveClick(v);
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.saveClickListener = listener;
    }

    public String getProductName() {
        return etProductName.getText().toString().trim();
    }

    public String getProductBrand() {
        return etProductBrand.getText().toString().trim();
    }

    public String getProductPrice() {
        return etProductPrice.getText().toString().trim();
    }

    public String getProductStock() {
        return etProductStock.getText().toString().trim();
    }
}