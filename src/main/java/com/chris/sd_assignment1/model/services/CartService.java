package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.entities.CartItem;
import com.chris.sd_assignment1.model.entities.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {

    private final List<CartItem> cartItems;

    public CartService() {
        this.cartItems = new ArrayList<>();
    }

    public void addItem(Item item, int quantity) {
        Optional<CartItem> existingItem = cartItems.stream()
                .filter(ci -> ci.getItem().getId().equals(item.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            cartItems.add(new CartItem(item, quantity));
        }
    }

    public void removeItem(Item item) {
        cartItems.removeIf(ci -> ci.getItem().getId().equals(item.getId()));
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double calculateTotal() {
        return cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
}