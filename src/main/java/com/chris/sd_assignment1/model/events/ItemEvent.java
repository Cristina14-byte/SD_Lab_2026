package com.chris.sd_assignment1.model.events;

import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.User;

public class ItemEvent {
    public enum EventType { CREATED, UPDATED, DELETED }

    private final EventType type;
    private final Item item;
    private final User user;

    public ItemEvent(EventType type, Item item, User user) {
        this.type = type;
        this.item = item;
        this.user = user;
    }

    public EventType getType() { return type; }
    public Item getItem() { return item; }
    public User getUser() { return user; }
}