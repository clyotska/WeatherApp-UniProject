package com.github.clyotska.weatherappuniproject.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String city;

    public User(String username, String password, String city) {
        this.username = username;
        this.password = password;
        this.city = city;
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
