module com.github.clyotska.weatherappuniproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.commons.lang3;

    opens com.github.clyotska.weatherappuniproject to javafx.fxml;
    exports com.github.clyotska.weatherappuniproject;
    exports com.github.clyotska.weatherappuniproject.controller;
    opens com.github.clyotska.weatherappuniproject.controller to javafx.fxml;
    exports com.github.clyotska.weatherappuniproject.model;
    opens com.github.clyotska.weatherappuniproject.service to com.google.gson;
    opens com.github.clyotska.weatherappuniproject.model to com.google.gson;
}