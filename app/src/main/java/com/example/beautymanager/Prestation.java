package com.example.beautymanager;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Prestation {
    private String id;
    private String nom;
    private String categorie;
    private double prix;
    private String duree;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Prestation() {
        // Constructeur vide requis pour Firestore
    }

    public Prestation(String nom, String categorie, double prix, String duree, String description) {
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.duree = duree;
        this.description = description;
    }

    // Getters
    public String getNom() { return nom; }
    public String getCategorie() { return categorie; }
    public double getPrix() { return prix; }
    public String getDuree() { return duree; }
    public String getDescription() { return description; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    @Exclude
    public String getId() { return id; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setDuree(String duree) { this.duree = duree; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}