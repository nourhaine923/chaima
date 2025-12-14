package com.example.beautymanager;

public class Cliente {
    private String id;
    private String nom;
    private String telephone;
    private String email;
    private String typeCheveux;
    private String longueurCheveux;
    private String serviceFavori;
    private boolean active = true;

    // Constructeurs
    public Cliente() {}

    public Cliente(String nom, String telephone, String email, String typeCheveux,
                   String longueurCheveux, String serviceFavori) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.typeCheveux = typeCheveux;
        this.longueurCheveux = longueurCheveux;
        this.serviceFavori = serviceFavori;
        this.active = true;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTypeCheveux() { return typeCheveux; }
    public void setTypeCheveux(String typeCheveux) { this.typeCheveux = typeCheveux; }

    public String getLongueurCheveux() { return longueurCheveux; }
    public void setLongueurCheveux(String longueurCheveux) { this.longueurCheveux = longueurCheveux; }

    public String getServiceFavori() { return serviceFavori; }
    public void setServiceFavori(String serviceFavori) { this.serviceFavori = serviceFavori; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}