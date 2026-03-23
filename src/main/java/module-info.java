module com.chris.sd_assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires org.postgresql.jdbc;

    exports com.chris.sd_assignment1.main;
    opens com.chris.sd_assignment1.main to javafx.graphics, javafx.fxml;

    exports com.chris.sd_assignment1.controller;
    opens com.chris.sd_assignment1.controller to javafx.fxml;

    exports com.chris.sd_assignment1.model.entities;
    opens com.chris.sd_assignment1.model.entities to javafx.base;

    exports com.chris.sd_assignment1.model.services;
}