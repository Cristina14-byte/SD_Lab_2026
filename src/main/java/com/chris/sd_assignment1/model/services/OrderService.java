package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.Order;
import com.chris.sd_assignment1.model.entities.OrderItem;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.repository.ItemRepository;
import com.chris.sd_assignment1.model.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    public Order placeOrder(User user, List<OrderItem> orderItems) {
        double totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            Optional<Item> itemOpt = itemRepository.findById(orderItem.getItem().getId());
            if (itemOpt.isEmpty()) {
                throw new IllegalArgumentException("Item not found: " + orderItem.getItem().getId());
            }

            Item dbItem = itemOpt.get();
            if (dbItem.getStockQuantity() < orderItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for item: " + dbItem.getName());
            }

            dbItem.setStockQuantity(dbItem.getStockQuantity() - orderItem.getQuantity());
            itemRepository.update(dbItem);

            totalPrice += orderItem.getPricePerUnit() * orderItem.getQuantity();
        }

        Order newOrder = new Order(
                null,
                user,
                LocalDateTime.now(),
                "COMPLETED",
                orderItems,
                totalPrice
        );

        return orderRepository.save(newOrder);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}