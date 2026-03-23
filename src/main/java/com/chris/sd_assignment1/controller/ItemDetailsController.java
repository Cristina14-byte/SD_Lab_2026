package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.CartService;
import com.chris.sd_assignment1.model.services.CategoryService;
import com.chris.sd_assignment1.model.services.ItemService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class ItemDetailsController {

    @FXML private ImageView itemImageView;
    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label basePriceLabel;
    @FXML private Label finalPriceLabel;
    @FXML private Label discountLabel;
    @FXML private Label stockLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Spinner<Integer> quantitySpinner;

    private Item currentItem;
    private User currentUser;
    private ItemService itemService;
    private CategoryService categoryService;
    private CartService cartService;

    public void initializeDependencies(Item item, User currentUser, ItemService itemService, CategoryService categoryService, CartService cartService) {
        this.currentItem = item;
        this.currentUser = currentUser;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.cartService = cartService;

        setupUI();
    }

    private void setupUI() {
        nameLabel.setText(currentItem.getName());

        String catName = "Unknown Category";
        if (currentItem.getCategory() != null) {
            if (currentItem.getCategory().getName() != null) {
                catName = currentItem.getCategory().getName();
            } else if (currentItem.getCategory().getId() != null) {
                Category fullCategory = categoryService.getAllCategories().stream()
                        .filter(c -> c.getId().equals(currentItem.getCategory().getId()))
                        .findFirst()
                        .orElse(null);
                if (fullCategory != null && fullCategory.getName() != null) {
                    catName = fullCategory.getName();
                }
            }
        }
        categoryLabel.setText("Category: " + catName);

        descriptionArea.setText(currentItem.getDescription());
        stockLabel.setText("Stock available: " + currentItem.getStockQuantity());

        finalPriceLabel.setText(String.format("€%.2f", currentItem.getFinalPrice()));

        if (currentItem.getDiscountPercentage() > 0) {
            basePriceLabel.setText(String.format("€%.2f", currentItem.getBasePrice()));
            basePriceLabel.setVisible(true);
            basePriceLabel.setManaged(true);

            discountLabel.setText(String.format("(%.0f%% OFF)", currentItem.getDiscountPercentage()));
            discountLabel.setVisible(true);
            discountLabel.setManaged(true);
        } else {
            basePriceLabel.setVisible(false);
            basePriceLabel.setManaged(false);
            discountLabel.setVisible(false);
            discountLabel.setManaged(false);
        }

        int maxStock = currentItem.getStockQuantity();
        if (maxStock > 0) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxStock, 1);
            quantitySpinner.setValueFactory(valueFactory);
        } else {
            quantitySpinner.setDisable(true);
        }

        if (currentItem.getImageData() != null && currentItem.getImageData().length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(currentItem.getImageData());
                Image image = new Image(bis);
                itemImageView.setImage(image);
            } catch (Exception e) {
                loadPlaceholderImage();
            }
        } else {
            loadPlaceholderImage();
        }
    }

    private void loadPlaceholderImage() {
        try {
            String path = "/com/chris/sd_assignment1/images/placeholder.png";
            Image placeholder = new Image(getClass().getResourceAsStream(path));
            itemImageView.setImage(placeholder);
        } catch (Exception e) {
        }
    }

    @FXML
    protected void onAddToCartClick(ActionEvent event) {
        if (currentItem.getStockQuantity() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Out of Stock", "This item is currently out of stock.");
            return;
        }

        int selectedQuantity = quantitySpinner.getValue();
        cartService.addItem(currentItem, selectedQuantity);
        showAlert(Alert.AlertType.INFORMATION, "Success", selectedQuantity + "x " + currentItem.getName() + " added to your cart!");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}