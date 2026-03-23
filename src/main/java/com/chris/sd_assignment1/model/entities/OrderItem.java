package com.chris.sd_assignment1.model.entities;

public class OrderItem {
    private final Long id;
    private final Item item;
    private final int quantity;
    private final double pricePerUnit;

    public OrderItem(Long id, Item item, int quantity, double pricePerUnit) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }
}