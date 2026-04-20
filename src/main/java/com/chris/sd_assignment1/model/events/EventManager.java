package com.chris.sd_assignment1.model.events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void notify(ItemEvent event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}