package com.chris.sd_assignment1.model.repository;

import com.chris.sd_assignment1.model.entities.Item;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findByCategory_Id(Long categoryId);
}