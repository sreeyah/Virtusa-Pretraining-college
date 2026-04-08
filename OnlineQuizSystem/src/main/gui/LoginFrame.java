package gui;

import service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private String role;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthService authService;

    public LoginFrame(String role) {
        this.role = role;
        authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle(role.equals("admin") ? "Admin Login" : "User Login / Register");
        setSize(450, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(role.equals("admin") ? "Admin Login" : "User Login / Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        registerButton = new JButton("Register");
        registerButton.setVisible(role.equals("user"));
        panel.add(registerButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JButton backButton = new JButton("Back");
        panel.add(backButton, gbc);
        gbc.gridx = 1;
        JButton exitButton = new JButton("Exit");
        panel.add(exitButton, gbc);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RoleSelectionFrame().setVisible(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        String roleResult = authService.login(username, password);
        if (roleResult != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            if (roleResult.equals("admin")) {
                new AdminFrame().setVisible(true);
            } else {
                new UserFrame(username).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials! Please check username and password.");
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (authService.register(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist or database error occurred.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoleSelectionFrame().setVisible(true));
    }
}