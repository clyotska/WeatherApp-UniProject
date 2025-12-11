package com.github.clyotska.weatherappuniproject.model;

import org.apache.commons.lang3.StringUtils;

public class User {
    private int id;
    private String username;
    private String password;
    private String city;

    public User(String username, String password, String city) {
        this.username = username;
        this.password = password;
        this.city = StringUtils.capitalize(city);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
