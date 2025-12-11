package com.github.clyotska.weatherappuniproject.model;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;

public class ForecastDay {
    private final LocalDate date;
    private final double minTemp;
    private final double maxTemp;
    private final double humidity;
    private final String description;
    private final String icon;

    public ForecastDay(LocalDate date, double minTemp, double maxTemp, double humidity, String description, String icon) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.description = description;
        this.icon = icon;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return StringUtils.capitalize(description);
    }

    public String getIcon() {
        return icon;
    }
}

