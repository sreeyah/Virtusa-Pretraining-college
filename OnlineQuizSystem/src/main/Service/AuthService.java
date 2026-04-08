package service;

import db.DBConnection;
import java.sql.*;
import java.security.MessageDigest;

public class AuthService {

    public AuthService() {
        ensureAdminExists();
    }

    private void ensureAdminExists() {
        String selectAdmin = "SELECT id FROM users WHERE role='admin' LIMIT 1";
        String insertAdmin = "INSERT INTO users(username, password, role) VALUES ('admin', ?, 'admin')";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("Database connection failed while ensuring admin exists.");
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(selectAdmin);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                try (PreparedStatement insert = con.prepareStatement(insertAdmin)) {
                    insert.setString(1, hashPassword("admin"));
                    insert.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to ensure admin user exists.");
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        String query = "INSERT INTO users(username, password, role) VALUES (?, ?, 'user')";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("Database connection failed during registration.");
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Username already exists: " + username);
            return false;
        } catch (Exception e) {
            System.err.println("Registration error:");
            e.printStackTrace();
            return false;
        }
    }

    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String query = "SELECT role FROM users WHERE username=? AND password=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("Database connection failed during login.");
            return null;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
