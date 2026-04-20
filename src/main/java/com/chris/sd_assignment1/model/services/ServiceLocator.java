package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.repository.CategoryRepositoryImpl;
import com.chris.sd_assignment1.model.repository.ItemRepositoryImpl;
import com.chris.sd_assignment1.model.repository.UserRepositoryImpl;
import com.chris.sd_assignment1.model.events.EventManager;

public class ServiceLocator {
    private static ServiceLocator instance;

    private final UserService userService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final EventManager eventManager;
    private final NotificationService notificationService;

    private ServiceLocator() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        ItemRepositoryImpl itemRepository = new ItemRepositoryImpl();
        CategoryRepositoryImpl categoryRepository = new CategoryRepositoryImpl();

        this.eventManager = new EventManager();
        this.notificationService = new NotificationService();
        this.eventManager.subscribe(notificationService);

        this.userService = new UserService(userRepository);
        this.itemService = new ItemService(itemRepository, eventManager);
        this.categoryService = new CategoryService(categoryRepository);
        this.cartService = new CartService();
    }

    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public UserService getUserService() { return userService; }
    public ItemService getItemService() { return itemService; }
    public CategoryService getCategoryService() { return categoryService; }
    public CartService getCartService() { return cartService; }
}