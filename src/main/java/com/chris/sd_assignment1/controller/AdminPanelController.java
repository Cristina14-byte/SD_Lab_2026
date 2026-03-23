package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.CartService;
import com.chris.sd_assignment1.model.services.CategoryService;
import com.chris.sd_assignment1.model.services.ItemService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPanelController {

    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, Long> itemIdCol;
    @FXML private TableColumn<Item, String> itemNameCol;
    @FXML private TableColumn<Item, String> itemCategoryCol;
    @FXML private TableColumn<Item, Double> itemPriceCol;
    @FXML private TableColumn<Item, Integer> itemStockCol;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Long> categoryIdCol;
    @FXML private TableColumn<Category, String> categoryNameCol;
    @FXML private TableColumn<Category, String> categoryDescCol;

    private User currentUser;
    private ItemService itemService;
    private CategoryService categoryService;
    private CartService cartService;

    public void initializeDependencies(User user, ItemService itemService, CategoryService categoryService, CartService cartService) {
        this.currentUser = user;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.cartService = cartService;

        setupTables();
        loadItems();
        loadCategories();
    }

    private void setupTables() {
        itemIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        itemNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        itemCategoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
        itemPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getBasePrice()).asObject());
        itemStockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());

        categoryIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        categoryNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        categoryDescCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void loadItems() {
        ObservableList<Item> itemsList = FXCollections.observableArrayList(itemService.getAllItems());
        itemsTable.setItems(itemsList);
    }

    private void loadCategories() {
        ObservableList<Category> categoriesList = FXCollections.observableArrayList(categoryService.getAllCategories());
        categoriesTable.setItems(categoriesList);
    }

    @FXML
    protected void onAddItemClick(ActionEvent event) {
        openItemForm(null);
    }

    @FXML
    protected void onEditItemClick(ActionEvent event) {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to edit.");
            return;
        }
        openItemForm(selectedItem);
    }

    @FXML
    protected void onDeleteItemClick(ActionEvent event) {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to delete.");
            return;
        }
        try {
            itemService.deleteItem(selectedItem.getId(), currentUser);
            loadItems();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    protected void onAddCategoryClick(ActionEvent event) {
        openCategoryForm(null);
    }

    @FXML
    protected void onEditCategoryClick(ActionEvent event) {
        Category selectedCategory = categoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a category to edit.");
            return;
        }
        openCategoryForm(selectedCategory);
    }

    @FXML
    protected void onDeleteCategoryClick(ActionEvent event) {
        Category selectedCategory = categoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a category to delete.");
            return;
        }
        try {
            categoryService.deleteCategory(selectedCategory.getId());
            loadCategories();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    protected void onBackToShopClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/main_shop.fxml"));
            Parent root = loader.load();

            MainShopController mainShopController = loader.getController();
            mainShopController.initializeDependencies(currentUser, itemService, categoryService, cartService);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1080, 720));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the shop page.");
        }
    }

    private void openItemForm(Item itemToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/item_form.fxml"));
            Parent root = loader.load();

            ItemFormController controller = loader.getController();
            controller.initializeDependencies(itemService, categoryService, currentUser);
            controller.setOnSaveCallback(this::loadItems);
            controller.setItemForEdit(itemToEdit);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(itemToEdit == null ? "Add Item" : "Edit Item");
            stage.setScene(new Scene(root, 450, 600));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open item form.");
        }
    }

    private void openCategoryForm(Category categoryToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/category_form.fxml"));
            Parent root = loader.load();

            CategoryFormController controller = loader.getController();
            controller.setCategoryService(categoryService);
            controller.setOnSaveCallback(this::loadCategories);
            controller.setCategoryForEdit(categoryToEdit);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(categoryToEdit == null ? "Add Category" : "Edit Category");
            stage.setScene(new Scene(root, 400, 350));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open category form.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}