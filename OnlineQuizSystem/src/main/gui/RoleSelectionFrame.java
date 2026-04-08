package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoleSelectionFrame extends JFrame {
    public RoleSelectionFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Quiz System - Role Selection");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel label = new JLabel("Login as Admin or User");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JButton adminButton = new JButton("Admin Login");
        panel.add(adminButton, gbc);
        gbc.gridx = 1;
        JButton userButton = new JButton("User Login/Register");
        panel.add(userButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton exitButton = new JButton("Exit");
        panel.add(exitButton, gbc);

        add(panel);

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame("admin").setVisible(true);
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame("user").setVisible(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}