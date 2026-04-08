package gui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

class StudyQuestion {
    String text;
    String[] options;
    int correct;

    StudyQuestion(String text, String[] options, int correct) {
        this.text = text;
        this.options = options;
        this.correct = correct;
    }
}

public class StudyFrame extends JFrame {
    private List<StudyQuestion> questions;
    private String username;
    private int currentIndex = 0;

    private JLabel titleLabel;
    private JTextArea questionArea;
    private JButton prevButton;
    private JButton nextButton;
    private JButton modeButton;
    private JButton exitButton;

    public StudyFrame(String username, String difficulty) {
        this.username = username;
        questions = loadQuestions(difficulty);
        initializeUI();
        if (!questions.isEmpty()) {
            displayQuestion();
        }
    }

    private List<StudyQuestion> loadQuestions(String difficulty) {
        List<StudyQuestion> list = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE difficulty=? ORDER BY id LIMIT 30";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return list;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, difficulty);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String text = rs.getString("question_text");
                    String[] options = {
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4")
                    };
                    int correct = rs.getInt("correct_option");
                    list.add(new StudyQuestion(text, options, correct));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void initializeUI() {
        setTitle("Study Mode");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        titleLabel = new JLabel("Study Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        add(new JScrollPane(questionArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        modeButton = new JButton("Mode Select");
        exitButton = new JButton("Exit");
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(modeButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayQuestion();
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    displayQuestion();
                }
            }
        });

        modeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new UserFrame(username).setVisible(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        updateButtons();
    }

    private void displayQuestion() {
        if (questions.isEmpty()) {
            titleLabel.setText("No questions available for this difficulty.");
            questionArea.setText("Please go back and choose a different difficulty or ask the admin to add questions.");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }

        StudyQuestion question = questions.get(currentIndex);
        titleLabel.setText("Study Mode - Question " + (currentIndex + 1) + " of " + questions.size());
        StringBuilder sb = new StringBuilder();
        sb.append(question.text).append("\n\n");
        for (int i = 0; i < question.options.length; i++) {
            sb.append((i + 1)).append(". ").append(question.options[i]);
            if (i + 1 == question.correct) {
                sb.append("  <- Correct Answer");
            }
            sb.append("\n");
        }
        questionArea.setText(sb.toString());
        updateButtons();
    }

    private void updateButtons() {
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(!questions.isEmpty() && currentIndex < questions.size() - 1);
    }
}