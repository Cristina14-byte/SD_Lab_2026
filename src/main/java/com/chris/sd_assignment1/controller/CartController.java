package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.CartItem;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.CartService;
import com.chris.sd_assignment1.model.services.CategoryService;
import com.chris.sd_assignment1.model.services.ItemService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> nameCol;
    @FXML private TableColumn<CartItem, Double> unitPriceCol;
    @FXML private TableColumn<CartItem, Integer> quantityCol;
    @FXML private TableColumn<CartItem, Double> totalPriceCol;
    @FXML private Label grandTotalLabel;

    private User currentUser;
    private ItemService itemService;
    private CategoryService categoryService;
    private CartService cartService;

    public void initializeDependencies(User currentUser, ItemService itemService, CategoryService categoryService, CartService cartService) {
        this.currentUser = currentUser;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.cartService = cartService;

        setupTable();
        loadCart();
    }

    private void setupTable() {
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getName()));
        unitPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getItem().getFinalPrice()).asObject());
        quantityCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        totalPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
    }

    private void loadCart() {
        ObservableList<CartItem> items = FXCollections.observableArrayList(cartService.getCartItems());
        cartTable.setItems(items);
        grandTotalLabel.setText(String.format("€%.2f", cartService.calculateTotal()));
    }

    @FXML
    protected void onRemoveItemClick(ActionEvent event) {
        CartItem selectedCartItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedCartItem == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to remove.");
            return;
        }

        cartService.removeItem(selectedCartItem.getItem());
        loadCart();
    }

    @FXML
    protected void onCheckoutClick(ActionEvent event) {
        if (cartService.getCartItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Your cart is empty.");
            return;
        }

        try {
            for (CartItem cartItem : cartService.getCartItems()) {
                itemService.purchaseItem(cartItem.getItem().getId(), cartItem.getQuantity());
            }

            cartService.clearCart();
            loadCart();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Purchase completed successfully!");

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Checkout Error", e.getMessage());
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the shop page.");
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