package com.github.clyotska.weatherappuniproject.model;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class WeatherData {
    private String cityName;

    private MainData main;

    private List<WeatherDescription> weather;

    public String getCityName() {return cityName;}
    public double getTemp(){
        if (main == null) {
            return 0;
        }
        return main.temp;
    }
    public double getHumidity() {
        if (main != null) {
            return main.humidity;
        }
        return 0;
    }
    public String getDescription(){
        if (weather != null && !weather.isEmpty()){
            return StringUtils.capitalize(weather.getFirst().description);
        }
        return "";
    }
    public static class MainData {
        public double temp;
        public double humidity;
    }

    public static class WeatherDescription {
        public String description;
        public String icon;
    }

}
