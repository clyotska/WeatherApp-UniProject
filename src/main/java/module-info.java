module com.github.clyotska.weatherappuniproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.github.clyotska.weatherappuniproject to javafx.fxml;
    exports com.github.clyotska.weatherappuniproject;
}