package com.chris.sd_assignment1.model.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final Long id;
    private final User user;
    private final LocalDateTime orderDate;
    private final String status;
    private final List<OrderItem> items;
    private final double totalPrice;

    public Order(Long id, User user, LocalDateTime orderDate, String status, List<OrderItem> items, double totalPrice) {
        this.id = id;
        this.user = user;
        this.orderDate = orderDate;
        this.status = status;
        this.items = List.copyOf(items);
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}