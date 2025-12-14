package com.example.beautymanager;

import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class RendezVous {
    private String id; // Utilisé pour l'ID Firestore
    private Date date;
    private String heure;
    private String statut; // "Validé", "En attente", "Annulé"
    private String clientId;
    private String clientNom;
    private String prestationId;
    private String prestationNom;

    public RendezVous() {
        // Constructeur vide requis pour Firestore
    }

    public RendezVous(Date date, String heure, String statut, String clientId, String clientNom, String prestationId, String prestationNom) {
        this.date = date;
        this.heure = heure;
        this.statut = statut;
        this.clientId = clientId;
        this.clientNom = clientNom;
        this.prestationId = prestationId;
        this.prestationNom = prestationNom;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getPrestationId() {
        return prestationId;
    }

    public void setPrestationId(String prestationId) {
        this.prestationId = prestationId;
    }

    public String getPrestationNom() {
        return prestationNom;
    }

    public void setPrestationNom(String prestationNom) {
        this.prestationNom = prestationNom;
    }
}
