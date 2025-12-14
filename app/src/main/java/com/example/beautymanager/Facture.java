package com.example.beautymanager;

import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class Facture {
    private String id;
    private String numeroFacture;
    private String clientNom;
    private double montant;
    private Date date;
    private String statut;

    public Facture() {
        // Constructeur vide requis pour Firestore
    }

    public Facture(String numeroFacture, String clientNom, double montant, Date date, String statut) {
        this.numeroFacture = numeroFacture;
        this.clientNom = clientNom;
        this.montant = montant;
        this.date = date;
        this.statut = statut;
    }

    // Getters
    @Exclude
    public String getId() { return id; }
    public String getNumeroFacture() { return numeroFacture; }
    public String getClientNom() { return clientNom; }
    public double getMontant() { return montant; }
    public Date getDate() { return date; }
    public String getStatut() { return statut; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setDate(Date date) { this.date = date; }
    public void setStatut(String statut) { this.statut = statut; }
}