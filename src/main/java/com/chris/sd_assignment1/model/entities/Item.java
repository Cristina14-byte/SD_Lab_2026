package com.chris.sd_assignment1.model.entities;

import java.time.LocalDate;

public class Item {
    private Long id;
    private String name;
    private Category category;
    private double basePrice;
    private double discountPercentage;
    private int stockQuantity;
    private String description;
    private LocalDate dateAdded;
    private LocalDate releaseDate;
    private byte[] imageData;

    public Item(){
    }

    public Item(Long id, String name, Category category, double basePrice, double discountPercentage, int stockQuantity, String description, LocalDate dateAdded, LocalDate releaseDate, byte[] imageData) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.discountPercentage = discountPercentage;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.dateAdded = dateAdded;
        this.releaseDate = releaseDate;
        this.imageData = imageData;
    }

    public double getFinalPrice() {
        return basePrice - (basePrice * (discountPercentage / 100.0));
    }

    public boolean isPreorder() {
        return releaseDate != null && releaseDate.isAfter(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.discountPercentage = discountPercentage;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}