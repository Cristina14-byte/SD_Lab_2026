package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.services.CategoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryFormController {

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;

    private CategoryService categoryService;
    private Category currentCategory;
    private Runnable onSaveCallback;

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    public void setCategoryForEdit(Category category) {
        this.currentCategory = category;
        if (category != null) {
            formTitle.setText("Edit Category");
            nameField.setText(category.getName());
            descriptionField.setText(category.getDescription());
        }
    }

    @FXML
    protected void onSaveClick(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category name cannot be empty.");
            return;
        }

        try {
            if (currentCategory == null) {
                Category newCategory = new Category();
                newCategory.setName(name);
                newCategory.setDescription(description);
                categoryService.createCategory(newCategory);
            } else {
                currentCategory.setName(name);
                currentCategory.setDescription(description);
                categoryService.updateCategory(currentCategory);
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            closeWindow(event);

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