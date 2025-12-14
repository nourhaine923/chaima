package com.example.beautymanager;

import java.util.Date;

public class Paiement {
    private int idPaiement;
    private String modePaiement; // "Espèces", "Carte bancaire", "Chèque", "Virement"
    private Date date;
    private double montant;
    private String etat; // "Effectué", "En attente", "Annulé"

    public Paiement(int idPaiement, String modePaiement, Date date, double montant, String etat) {
        this.idPaiement = idPaiement;
        this.modePaiement = modePaiement;
        this.date = date;
        this.montant = montant;
        this.etat = etat;
    }

    // Opération: effectuerPaiement
    public boolean effectuerPaiement() {
        this.etat = "Effectué";
        return true;
    }

    // Opération: annulerPaiement
    public boolean annulerPaiement() {
        this.etat = "Annulé";
        return true;
    }

    // Opération: setEtat
    public void setEtat(String etat) {
        this.etat = etat;
    }

    // Getters
    public int getIdPaiement() { return idPaiement; }
    public String getModePaiement() { return modePaiement; }
    public Date getDate() { return date; }
    public double getMontant() { return montant; }
    public String getEtat() { return etat; }

    // Setters
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public void setMontant(double montant) { this.montant = montant; }
}