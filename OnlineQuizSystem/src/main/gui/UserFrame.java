package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserFrame extends JFrame {
    private String username;
    private JComboBox<String> difficultyBox;
    private JButton quizButton;
    private JButton studyButton;

    public UserFrame(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User Panel - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Welcome, " + username + "!"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Select Difficulty:"), gbc);
        gbc.gridx = 1;
        difficultyBox = new JComboBox<>(new String[]{"easy", "medium", "hard"});
        panel.add(difficultyBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        quizButton = new JButton("Quiz Mode");
        panel.add(quizButton, gbc);
        gbc.gridx = 1;
        studyButton = new JButton("Study Mode");
        panel.add(studyButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JButton backButton = new JButton("Back");
        panel.add(backButton, gbc);
        gbc.gridx = 1;
        JButton exitButton = new JButton("Exit");
        panel.add(exitButton, gbc);

        add(panel);

        quizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String diff = (String) difficultyBox.getSelectedItem();
                dispose();
                new QuizFrame(username, diff).setVisible(true);
            }
        });

        studyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String diff = (String) difficultyBox.getSelectedItem();
                dispose();
                new StudyFrame(username, diff).setVisible(true);
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
}