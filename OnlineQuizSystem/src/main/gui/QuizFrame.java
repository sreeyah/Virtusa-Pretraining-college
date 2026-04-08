package gui;

import service.QuizService;
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

class Question {
    String text;
    String[] options;
    int correct;

    Question(String text, String[] options, int correct) {
        this.text = text;
        this.options = options;
        this.correct = correct;
    }
}

public class QuizFrame extends JFrame {
    private QuizService quizService;
    private String username;
    private List<Question> questions;
    private int currentIndex = 0;
    private int[] selectedAnswers;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup group;
    private JButton prevButton;
    private JButton nextButton;
    private JButton backButton;
    private JButton exitButton;

    public QuizFrame(String username, String difficulty) {
        this.username = username;
        quizService = new QuizService();
        questions = loadQuestions(difficulty);

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available for this difficulty.");
            dispose();
            return;
        }

        selectedAnswers = new int[questions.size()];
        initializeUI();
        displayQuestion();
    }

    private List<Question> loadQuestions(String difficulty) {
        List<Question> list = new ArrayList<>();
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
                    list.add(new Question(text, options, correct));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void initializeUI() {
        setTitle("Quiz Mode");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(questionLabel, gbc);

        optionButtons = new JRadioButton[4];
        group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            group.add(optionButtons[i]);
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.gridwidth = 2;
            add(optionButtons[i], gbc);
        }

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        backButton = new JButton("Mode Select");
        exitButton = new JButton("Exit");

        gbc.gridwidth = 1;
        gbc.gridy = 5;
        gbc.gridx = 0;
        add(prevButton, gbc);
        gbc.gridx = 1;
        add(nextButton, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        add(backButton, gbc);
        gbc.gridx = 1;
        add(exitButton, gbc);

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentSelection(false);
                if (currentIndex > 0) {
                    currentIndex--;
                    displayQuestion();
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!saveCurrentSelection(true)) {
                    JOptionPane.showMessageDialog(QuizFrame.this, "Please select an option before continuing.");
                    return;
                }
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    displayQuestion();
                } else {
                    showResult();
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
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
        Question q = questions.get(currentIndex);
        questionLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size() + ": " + q.text);
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.options[i]);
            optionButtons[i].setSelected(selectedAnswers[currentIndex] == i + 1);
        }
        updateButtons();
    }

    private boolean saveCurrentSelection(boolean requireSelection) {
        int selected = -1;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selected = i + 1;
                break;
            }
        }
        if (selected == -1) {
            if (requireSelection && selectedAnswers[currentIndex] == 0) {
                return false;
            }
            return true;
        }
        selectedAnswers[currentIndex] = selected;
        return true;
    }

    private void updateButtons() {
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setText(currentIndex == questions.size() - 1 ? "Finish" : "Next");
    }

    private void showResult() {
        int attempted = 0;
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            int selected = selectedAnswers[i];
            if (selected != 0) {
                attempted++;
                if (selected == questions.get(i).correct) {
                    correctCount++;
                }
            }
        }
        int wrongCount = attempted - correctCount;
        int score = correctCount * 5;
        quizService.saveResult(username, score, attempted, correctCount, wrongCount);

        double percentage = attempted == 0 ? 0 : (double) correctCount / attempted * 100;
        String quote = percentage >= 50 ?
            "\"Success is not final, failure is not fatal: It is the courage to continue that counts.\" - Winston Churchill" :
            "\"The only way to do great work is to love what you do.\" - Steve Jobs";
        String result = "Score: " + score + "\nAttempted: " + attempted + "\nCorrect: " + correctCount + "\nWrong: " + wrongCount + "\n\n" + quote;
        JOptionPane.showMessageDialog(this, result, "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}