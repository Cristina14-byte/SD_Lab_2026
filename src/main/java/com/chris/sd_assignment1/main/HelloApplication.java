package com.chris.sd_assignment1.main;

import com.chris.sd_assignment1.controller.LoginController;
import com.chris.sd_assignment1.model.services.ServiceLocator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/chris/sd_assignment1/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        stage.setTitle("Icey's TCG Shop");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}