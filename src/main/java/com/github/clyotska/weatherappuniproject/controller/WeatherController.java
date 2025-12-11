package com.github.clyotska.weatherappuniproject.controller;

import com.github.clyotska.weatherappuniproject.model.ForecastDay;
import com.github.clyotska.weatherappuniproject.model.WeatherData;
import com.github.clyotska.weatherappuniproject.service.WeatherService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField cityField;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;
    @FXML
    private FlowPane forecastContainer;
    @FXML
    private Label todayTitleLabel;
    @FXML
    private Label todayTempLabel;
    @FXML
    private Label todayHumidityLabel;
    @FXML
    private Label todayDescLabel;

    private final WeatherService weatherService = new WeatherService();
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E, MMM d");

    @FXML
    private void initialize() {
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");
    }

    public void setUser(com.github.clyotska.weatherappuniproject.model.User user) {
        welcomeLabel.setText("Welcome, " + user.getUsername());
        cityField.setText(user.getCity());
        loadForecast();
    }

    @FXML
    private void handleRefresh() {
        loadForecast();
    }

    private void loadForecast() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            statusLabel.setText("City cannot be empty.");
            return;
        }
        setStatus("Loading...", "black");
        refreshButton.setDisable(true);
        forecastContainer.getChildren().clear();
        clearToday();

        Task<WeatherBundle> task = new Task<>() {
            @Override
            protected WeatherBundle call() throws Exception {
                WeatherData current = weatherService.getWeatherData(city);
                List<ForecastDay> forecast = weatherService.getFiveDayForecast(city);
                return new WeatherBundle(current, forecast);
            }
        };

        task.setOnSucceeded(ev -> {
            WeatherBundle bundle = task.getValue();
            List<ForecastDay> days = bundle.forecast();
            setStatus("Loaded forecast.", "green");
            refreshButton.setDisable(false);
            renderForecast(days);
            renderToday(bundle.current());
        });

        task.setOnFailed(ev -> {
            Throwable ex = ev.getSource().getException();
            String message = ex != null && ex.getMessage() != null
                    ? ex.getMessage()
                    : "Failed to load forecast.";
            setStatus(message, "red");
            refreshButton.setDisable(false);
            ex.printStackTrace();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderForecast(List<ForecastDay> days) {
        forecastContainer.getChildren().clear();

        for (ForecastDay day : days) {
            VBox card = new VBox(4);
            card.setStyle("-fx-padding: 8; -fx-border-color: lightgray; -fx-border-radius: 6; -fx-background-radius: 6;");

            Label date = new Label(day.getDate().format(dayFormatter));
            date.setStyle("-fx-font-weight: bold;");

            Label temp = new Label(String.format("Min %.0f°C / Max %.0f°C", day.getMinTemp(), day.getMaxTemp()));
            Label humidity = new Label(String.format("Humidity: %.0f%%", day.getHumidity()));
            Label desc = new Label(day.getDescription());

            card.getChildren().addAll(date, temp, humidity, desc);
            forecastContainer.getChildren().add(card);
        }
    }

    private void renderToday(WeatherData current) {
        if (current == null) {
            todayTitleLabel.setText("Today");
            todayTempLabel.setText("—");
            todayHumidityLabel.setText("—");
            todayDescLabel.setText("");
            return;
        }
        todayTitleLabel.setText("Today in " + cityField.getText().trim());
        todayTempLabel.setText(String.format("Temp: %.0f°C", current.getTemp()));
        todayHumidityLabel.setText(String.format("Humidity: %.0f%%", current.getHumidity()));
        todayDescLabel.setText(current.getDescription());
    }

    private void clearToday() {
        todayTitleLabel.setText("Today");
        todayTempLabel.setText("");
        todayHumidityLabel.setText("");
        todayDescLabel.setText("");
    }

    private static class WeatherBundle {
        private final WeatherData current;
        private final List<ForecastDay> forecast;

        WeatherBundle(WeatherData current, List<ForecastDay> forecast) {
            this.current = current;
            this.forecast = forecast;
        }

        public WeatherData current() {
            return current;
        }

        public List<ForecastDay> forecast() {
            return forecast;
        }
    }

    private void setStatus(String text, String color) {
        statusLabel.setText(text);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
