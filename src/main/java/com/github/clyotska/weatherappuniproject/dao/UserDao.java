package com.github.clyotska.weatherappuniproject.dao;

import com.github.clyotska.weatherappuniproject.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to check if username exists");
            e.printStackTrace();
            return true; // treat as existing to prevent duplicates on failure
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, city FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String u = resultSet.getString("username");
                    String p = resultSet.getString("password");
                    String c = resultSet.getString("city");
                    return new User(u, p, c);
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to fetch user by username");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValidCredentials(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return storedPassword != null && storedPassword.equals(password);
                }
                return false;
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to validate credentials");
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, city) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getCity());
            return statement.executeUpdate() == 1;
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to insert user");
            e.printStackTrace();
            return false;
        }
    }
}


