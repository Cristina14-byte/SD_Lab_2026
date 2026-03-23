package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.CategoryService;
import com.chris.sd_assignment1.model.services.ItemService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class ItemFormController {

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private DatePicker releaseDatePicker;
    @FXML private TextField priceField;
    @FXML private TextField discountField;
    @FXML private TextField stockField;
    @FXML private TextArea descriptionField;
    @FXML private ImageView imageView;

    private ItemService itemService;
    private CategoryService categoryService;
    private User currentUser;
    private Item currentItem;
    private Runnable onSaveCallback;
    private byte[] currentImageData;

    public void initializeDependencies(ItemService itemService, CategoryService categoryService, User currentUser) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.currentUser = currentUser;
        loadCategories();
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    public void setItemForEdit(Item item) {
        this.currentItem = item;
        if (item != null) {
            formTitle.setText("Edit Item");
            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getBasePrice()));
            discountField.setText(String.valueOf(item.getDiscountPercentage()));
            stockField.setText(String.valueOf(item.getStockQuantity()));
            descriptionField.setText(item.getDescription());
            releaseDatePicker.setValue(item.getReleaseDate());

            for (Category cat : categoryComboBox.getItems()) {
                if (cat.getId().equals(item.getCategory().getId())) {
                    categoryComboBox.getSelectionModel().select(cat);
                    break;
                }
            }

            if (item.getImageData() != null && item.getImageData().length > 0) {
                this.currentImageData = item.getImageData();
                imageView.setImage(new Image(new ByteArrayInputStream(currentImageData)));
            }
        }
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    @FXML
    protected void onChooseImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                currentImageData = Files.readAllBytes(selectedFile.toPath());
                imageView.setImage(new Image(new ByteArrayInputStream(currentImageData)));
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Image Error", "Could not load the selected image.");
            }
        }
    }

    @FXML
    protected void onSaveClick(ActionEvent event) {
        try {
            String name = nameField.getText();
            Category category = categoryComboBox.getValue();
            double price = Double.parseDouble(priceField.getText());
            double discount = Double.parseDouble(discountField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String description = descriptionField.getText();
            LocalDate releaseDate = releaseDatePicker.getValue();

            if (currentItem == null) {
                Item newItem = new Item();
                newItem.setName(name);
                newItem.setCategory(category);
                newItem.setBasePrice(price);
                newItem.setDiscountPercentage(discount);
                newItem.setStockQuantity(stock);
                newItem.setDescription(description);
                newItem.setDateAdded(LocalDate.now());
                newItem.setReleaseDate(releaseDate);
                newItem.setImageData(currentImageData);

                itemService.createItem(newItem, currentUser);
            } else {
                currentItem.setName(name);
                currentItem.setCategory(category);
                currentItem.setBasePrice(price);
                currentItem.setDiscountPercentage(discount);
                currentItem.setStockQuantity(stock);
                currentItem.setDescription(description);
                currentItem.setReleaseDate(releaseDate);

                if (currentImageData != null) {
                    currentItem.setImageData(currentImageData);
                }

                itemService.updateItem(currentItem, currentUser);
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            closeWindow(event);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers for price, discount, and stock.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    protected void onCancelClick(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
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