package com.github.clyotska.weatherappuniproject.service;

import com.github.clyotska.weatherappuniproject.model.ForecastDay;
import com.github.clyotska.weatherappuniproject.model.WeatherData;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class WeatherService {
    private static final String API_KEY = "a33d4c3d8a9555ed337de1c9ed3a5e20";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric";
    private static final Gson GSON = new Gson();

    public WeatherData getWeatherData(String city) throws Exception {
        String json = fetchJson(String.format(API_URL, city, API_KEY), "weather data");
        return GSON.fromJson(json, WeatherData.class);
    }

    public List<ForecastDay> getFiveDayForecast(String city) throws Exception {
        String json = fetchJson(String.format(FORECAST_URL, city, API_KEY), "forecast data");
        ForecastApiResponse apiResponse = GSON.fromJson(json, ForecastApiResponse.class);
        return mapToDaily(apiResponse);
    }

    private String fetchJson(String urlString, String label) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            if (responseCode == 404) {
                throw new Exception("City not found. Please check the spelling.");
            }
            throw new Exception("Failed to get " + label + ": HTTP " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private List<ForecastDay> mapToDaily(ForecastApiResponse apiResponse) {
        List<ForecastDay> days = new ArrayList<>();
        if (apiResponse == null || apiResponse.list == null) {
            return days;
        }

        Map<LocalDate, List<ForecastApiResponse.ForecastItem>> byDate = new HashMap<>();

        for (ForecastApiResponse.ForecastItem item : apiResponse.list) {
            LocalDate date = Instant.ofEpochSecond(item.dt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            List<ForecastApiResponse.ForecastItem> itemsForDate = byDate.get(date);
            if (itemsForDate == null) {
                itemsForDate = new ArrayList<>();
                byDate.put(date, itemsForDate);
            }
            itemsForDate.add(item);
        }

        List<LocalDate> sortedDates = new ArrayList<>(byDate.keySet());
        sortedDates.sort(LocalDate::compareTo);

        int addedDays = 0;
        for (LocalDate date : sortedDates) {
            if (addedDays >= 5) {
                break;
            }

            List<ForecastApiResponse.ForecastItem> items = byDate.get(date);
            if (items == null || items.isEmpty()) {
                continue;
            }

            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            double humiditySum = 0.0;
            int humidityCount = 0;

            for (ForecastApiResponse.ForecastItem item : items) {
                if (item.main != null) {
                    if (item.main.temp_min != null) {
                        min = Math.min(min, item.main.temp_min);
                    }
                    if (item.main.temp_max != null) {
                        max = Math.max(max, item.main.temp_max);
                    }
                    if (item.main.humidity != null) {
                        humiditySum += item.main.humidity;
                        humidityCount++;
                    }
                }
            }

            if (min == Double.MAX_VALUE) {
                min = 0.0;
            }
            if (max == -Double.MAX_VALUE) {
                max = 0.0;
            }
            double avgHumidity = humidityCount > 0 ? humiditySum / humidityCount : 0.0;

            String description = "";
            String icon = "";
            ForecastApiResponse.ForecastItem firstItem = items.get(0);
            if (firstItem.weather != null && !firstItem.weather.isEmpty()) {
                ForecastApiResponse.WeatherDesc desc = firstItem.weather.get(0);
                if (desc != null) {
                    description = desc.description != null ? desc.description : "";
                    icon = desc.icon != null ? desc.icon : "";
                }
            }

            days.add(new ForecastDay(date, min, max, avgHumidity, description, icon));
            addedDays++;
        }

        return days;
    }

    public static class ForecastApiResponse {
        public List<ForecastItem> list;

        public static class ForecastItem {
            public long dt;
            public Main main;
            public List<WeatherDesc> weather;
        }

        public static class Main {
            public Double temp_min;
            public Double temp_max;
            public Double humidity;
        }

        public static class WeatherDesc {
            public String description;
            public String icon;
        }
    }
}
