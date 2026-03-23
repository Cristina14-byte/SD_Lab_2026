package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Role;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.ServiceLocator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            User user = ServiceLocator.getInstance().getUserService().authenticate(username, password).orElse(null);

            if (user != null) {
                goToMainShop(user, event);
            } else {
                showError("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showError("System Error", "An error occurred during login: " + e.getMessage());
        }
    }

    @FXML
    protected void onContinueAsGuestClick(ActionEvent event) {
        User guestUser = new User();
        guestUser.setId(0L);
        guestUser.setUsername("Guest");
        guestUser.setRole(Role.VISITOR);

        goToMainShop(guestUser, event);
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load the register page.");
        }
    }

    private void goToMainShop(User user, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/main_shop.fxml"));
            Parent root = loader.load();

            MainShopController mainShopController = loader.getController();
            mainShopController.initializeDependencies(
                    user,
                    ServiceLocator.getInstance().getItemService(),
                    ServiceLocator.getInstance().getCategoryService(),
                    ServiceLocator.getInstance().getCartService()
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1080, 720));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load the main shop page.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}