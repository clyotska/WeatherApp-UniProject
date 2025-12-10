package com.github.clyotska.weatherappuniproject.controller;

import com.github.clyotska.weatherappuniproject.dao.UserDao;
import com.github.clyotska.weatherappuniproject.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Locale;

public class SignUpController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Button signUpButton;
    @FXML
    private TextField cityTextField;
    @FXML
    private Label feedbackLabel;

    private final UserDao userDao = new UserDao();

    @FXML
    private void initialize() {
        clearFeedback();
    }

    @FXML
    private void handleSave() {
        clearFeedback();

        String username = usernameTextField.getText().trim();
        String password = passwordPasswordField.getText();
        String city = cityTextField.getText().toLowerCase(Locale.ROOT).trim();
        cityTextField.setText(city);

        boolean hasError = false;

        if (password.length() < 8) {
            feedbackLabel.setText("Password must be at least 8 characters.");
            passwordPasswordField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (username.isEmpty()) {
            appendFeedback("Username cannot be empty.");
            usernameTextField.setStyle("-fx-border-color: red;");
            hasError = true;
        } else if (userDao.usernameExists(username)) {
            appendFeedback("Username already exists.");
            usernameTextField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (city.isEmpty()) {
            appendFeedback("City cannot be empty.");
            cityTextField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        User user = new User(username, password, city);
        boolean saved = userDao.addUser(user);

        if (saved) {
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("User saved successfully.");
            usernameTextField.clear();
            passwordPasswordField.clear();
            cityTextField.clear();
        } else {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Failed to save user. Please try again.");
        }
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
        feedbackLabel.setStyle("-fx-text-fill: red;");
        usernameTextField.setStyle("");
        passwordPasswordField.setStyle("");
        cityTextField.setStyle("");
    }

    private void appendFeedback(String message) {
        if (feedbackLabel.getText().isEmpty()) {
            feedbackLabel.setText(message);
        } else {
            feedbackLabel.setText(feedbackLabel.getText() + " " + message);
        }
    }
}
