package com.example.beautymanager;

public class Preference {
    private int idPreference;
    private String typeCheveux; // "Raides", "Ondulés", "Bouclés", "Crépus"
    private String longueurCheveux; // "Courts", "Mi-longs", "Longs"
    private String serviceFavoris;
    private String typeVisage; // "Ovale", "Rond", "Carré", "Long", "Coeur"

    public Preference(int idPreference, String typeCheveux, String longueurCheveux,
                      String serviceFavoris, String typeVisage) {
        this.idPreference = idPreference;
        this.typeCheveux = typeCheveux;
        this.longueurCheveux = longueurCheveux;
        this.serviceFavoris = serviceFavoris;
        this.typeVisage = typeVisage;
    }

    // Opérations
    public void ajouterPreference() {
        // Logic to add preference
    }

    public void modifierPreference() {
        // Logic to modify preference
    }

    public void supprimerPreference() {
        // Logic to delete preference
    }

    // Getters
    public int getIdPreference() { return idPreference; }
    public String getTypeCheveux() { return typeCheveux; }
    public String getLongueurCheveux() { return longueurCheveux; }
    public String getServiceFavoris() { return serviceFavoris; }
    public String getTypeVisage() { return typeVisage; }

    // Setters
    public void setTypeCheveux(String typeCheveux) { this.typeCheveux = typeCheveux; }
    public void setLongueurCheveux(String longueurCheveux) { this.longueurCheveux = longueurCheveux; }
    public void setServiceFavoris(String serviceFavoris) { this.serviceFavoris = serviceFavoris; }
    public void setTypeVisage(String typeVisage) { this.typeVisage = typeVisage; }
}