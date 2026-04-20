module com.chris.sd_assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires org.postgresql.jdbc;

    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires java.mail;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports com.chris.sd_assignment1.main;
    opens com.chris.sd_assignment1.main to javafx.graphics, javafx.fxml;

    exports com.chris.sd_assignment1.controller;
    opens com.chris.sd_assignment1.controller to javafx.fxml;

    exports com.chris.sd_assignment1.model.services;

    exports com.chris.sd_assignment1.model.entities;

    opens com.chris.sd_assignment1.model.entities to javafx.base, com.google.gson, com.fasterxml.jackson.databind;
}