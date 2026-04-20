package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.Role;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.repository.ItemRepository;
import com.chris.sd_assignment1.model.events.EventManager;
import com.chris.sd_assignment1.model.events.ItemEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemService {

    private final ItemRepository itemRepository;
    private final EventManager eventManager;

    public ItemService(ItemRepository itemRepository, EventManager eventManager) {
        this.itemRepository = itemRepository;
        this.eventManager = eventManager;
    }

    public Item createItem(Item item, User currentUser) {
        if (currentUser.getRole() == Role.VISITOR) {
            throw new SecurityException("Visitors cannot create items.");
        }
        validateItemFields(item);
        Item savedItem = itemRepository.save(item);
        eventManager.notify(new ItemEvent(ItemEvent.EventType.CREATED, savedItem, currentUser));
        return savedItem;
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item updateItem(Item item, User currentUser) {
        if (currentUser.getRole() == Role.VISITOR) {
            throw new SecurityException("Visitors cannot update items.");
        }
        validateItemFields(item);
        Item updatedItem = itemRepository.update(item);
        eventManager.notify(new ItemEvent(ItemEvent.EventType.UPDATED, updatedItem, currentUser));
        return updatedItem;
    }

    public void deleteItem(Long id, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("Only administrators can delete items.");
        }
        Item itemToDelete = itemRepository.findById(id).orElse(null);
        itemRepository.deleteById(id);
        eventManager.notify(new ItemEvent(ItemEvent.EventType.DELETED, itemToDelete, currentUser));
    }

    public List<Item> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategory_Id(categoryId);
    }

    public List<Item> getAllItemsSortedByPrice(boolean ascending) {
        List<Item> items = itemRepository.findAll();
        if (ascending) {
            items.sort(Comparator.comparingDouble(Item::getFinalPrice));
        } else {
            items.sort(Comparator.comparingDouble(Item::getFinalPrice).reversed());
        }
        return items;
    }

    public void purchaseItem(Long itemId, int quantity) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            if (item.getStockQuantity() >= quantity) {
                item.setStockQuantity(item.getStockQuantity() - quantity);
                itemRepository.update(item);
            } else {
                throw new IllegalArgumentException("Not enough stock available.");
            }
        } else {
            throw new IllegalArgumentException("Item not found.");
        }
    }

    private void validateItemFields(Item item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        if (item.getBasePrice() < 0) {
            throw new IllegalArgumentException("Item base price cannot be negative.");
        }
        if (item.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Item stock cannot be negative.");
        }
        if (item.getCategory() == null || item.getCategory().getId() == null) {
            throw new IllegalArgumentException("Item must have a valid category.");
        }
    }
}