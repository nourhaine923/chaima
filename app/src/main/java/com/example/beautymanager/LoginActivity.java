package com.example.beautymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> registerUser());
    }

    // ================= LOGIN =================

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mot de passe requis");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Toast.makeText(
                                this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Erreur de connexion",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // ================= REGISTER =================

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mot de passe requis");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Minimum 6 caractères");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Toast.makeText(
                                this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Erreur création du compte",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // ================= NAVIGATION =================

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
