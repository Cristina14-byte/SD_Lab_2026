package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.ServiceLocator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OrderSummaryController {

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    protected void onBackToShopClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/main_shop.fxml"));
            Parent root = loader.load();

            MainShopController mainShopController = loader.getController();
            mainShopController.initializeDependencies(
                    currentUser,
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
        }
    }
}