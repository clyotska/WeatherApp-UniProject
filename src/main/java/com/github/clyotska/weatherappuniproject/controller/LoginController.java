package com.github.clyotska.weatherappuniproject.controller;

import com.github.clyotska.weatherappuniproject.App;
import com.github.clyotska.weatherappuniproject.dao.UserDao;
import com.github.clyotska.weatherappuniproject.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Button goToSignUpButton;

    private final UserDao userDao = new UserDao();

    @FXML
    private void initialize() {
        clearFeedback();
    }

    @FXML
    private void handleLogin() {
        clearFeedback();

        String username = usernameTextField.getText().trim();
        String password = passwordPasswordField.getText();

        boolean hasError = false;

        if (username.isEmpty()) {
            appendFeedback("Username cannot be empty.");
            usernameTextField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (password.isEmpty()) {
            appendFeedback("Password cannot be empty.");
            passwordPasswordField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        if (!userDao.usernameExists(username)) {
            appendFeedback("No such username found.");
            usernameTextField.setStyle("-fx-border-color: red;");
            return;
        }

        boolean valid = userDao.isValidCredentials(username, password);
        if (valid) {
            User user = userDao.findByUsername(username);
            openWeatherScreen(user);
        } else {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Incorrect password.");
            passwordPasswordField.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    private void handleGoToSignUp() {
        switchScene("signup.fxml", "Sign Up");
    }

    private void switchScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
            Pane pane = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane, pane.getPrefWidth(), pane.getPrefHeight()));
            stage.setTitle(title);
        } catch (IOException e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Unable to open " + title + " screen.");
            e.printStackTrace();
        }
    }

    private void openWeatherScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("weather.fxml"));
            Pane pane = loader.load();
            WeatherController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(pane, pane.getPrefWidth(), pane.getPrefHeight()));
            stage.setTitle("Weather");
        } catch (IOException e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Unable to open weather screen.");
            e.printStackTrace();
        }
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
        feedbackLabel.setStyle("-fx-text-fill: red;");
        usernameTextField.setStyle("");
        passwordPasswordField.setStyle("");
    }

    private void appendFeedback(String message) {
        if (feedbackLabel.getText().isEmpty()) {
            feedbackLabel.setText(message);
        } else {
            feedbackLabel.setText(feedbackLabel.getText() + " " + message);
        }
    }
}
