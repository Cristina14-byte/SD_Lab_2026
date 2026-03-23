package com.chris.sd_assignment1.model.repository;

import com.chris.sd_assignment1.model.entities.Order;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}