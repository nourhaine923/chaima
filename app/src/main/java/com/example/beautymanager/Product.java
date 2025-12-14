package com.example.beautymanager;

public class Product {
    private String id;
    private String name;
    private String brand;
    private double price;
    private int stock;

    // Constructeur vide requis pour Firestore
    public Product() {}

    public Product(String name, String brand, double price, int stock) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}