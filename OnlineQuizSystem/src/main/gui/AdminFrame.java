package gui;

import service.AdminService;
import service.QuestionData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminFrame extends JFrame {
    private JTextField questionField;
    private JTextField option1Field, option2Field, option3Field, option4Field;
    private JComboBox<Integer> correctOptionBox;
    private JComboBox<String> difficultyBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton backButton;
    private JButton exitButton;
    private JTable questionTable;
    private DefaultTableModel tableModel;
    private AdminService adminService;
    private int selectedQuestionId = -1;

    public AdminFrame() {
        adminService = new AdminService();
        initializeUI();
        loadQuestions();
    }

    private void initializeUI() {
        setTitle("Admin Panel - Manage Questions");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(new JLabel("Question:"), gbc);
        gbc.gridy = 1;
        questionField = new JTextField(50);
        inputPanel.add(questionField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Option 1:"), gbc);
        gbc.gridx = 1;
        option1Field = new JTextField(25);
        inputPanel.add(option1Field, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Option 2:"), gbc);
        gbc.gridx = 1;
        option2Field = new JTextField(25);
        inputPanel.add(option2Field, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Option 3:"), gbc);
        gbc.gridx = 1;
        option3Field = new JTextField(25);
        inputPanel.add(option3Field, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Option 4:"), gbc);
        gbc.gridx = 1;
        option4Field = new JTextField(25);
        inputPanel.add(option4Field, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Correct Option:"), gbc);
        gbc.gridx = 1;
        correctOptionBox = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        inputPanel.add(correctOptionBox, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Difficulty:"), gbc);
        gbc.gridx = 1;
        difficultyBox = new JComboBox<>(new String[]{"easy", "medium", "hard"});
        inputPanel.add(difficultyBox, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        addButton = new JButton("Add Question");
        inputPanel.add(addButton, gbc);
        gbc.gridx = 1;
        updateButton = new JButton("Update Question");
        inputPanel.add(updateButton, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        backButton = new JButton("Back");
        inputPanel.add(backButton, gbc);
        gbc.gridx = 1;
        exitButton = new JButton("Exit");
        inputPanel.add(exitButton, gbc);

        tableModel = new DefaultTableModel(new String[]{"ID", "Question", "Option1", "Option2", "Option3", "Option4", "Correct", "Difficulty"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionTable = new JTable(tableModel);
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionTable.getColumnModel().getColumn(0).setMinWidth(40);
        questionTable.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane tableScrollPane = new JScrollPane(questionTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, tableScrollPane);
        splitPane.setDividerLocation(320);
        add(splitPane);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addQuestion();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateQuestion();
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

        questionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && questionTable.getSelectedRow() >= 0) {
                    selectQuestion(questionTable.getSelectedRow());
                }
            }
        });
    }

    private void loadQuestions() {
        tableModel.setRowCount(0);
        List<QuestionData> questions = adminService.getAllQuestions();
        for (QuestionData question : questions) {
            tableModel.addRow(new Object[]{
                question.getId(),
                question.getQuestionText(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4(),
                question.getCorrectOption(),
                question.getDifficulty()
            });
        }
    }

    private void selectQuestion(int row) {
        selectedQuestionId = (int) tableModel.getValueAt(row, 0);
        questionField.setText((String) tableModel.getValueAt(row, 1));
        option1Field.setText((String) tableModel.getValueAt(row, 2));
        option2Field.setText((String) tableModel.getValueAt(row, 3));
        option3Field.setText((String) tableModel.getValueAt(row, 4));
        option4Field.setText((String) tableModel.getValueAt(row, 5));
        correctOptionBox.setSelectedItem((Integer) tableModel.getValueAt(row, 6));
        difficultyBox.setSelectedItem((String) tableModel.getValueAt(row, 7));
    }

    private void addQuestion() {
        String question = questionField.getText().trim();
        String opt1 = option1Field.getText().trim();
        String opt2 = option2Field.getText().trim();
        String opt3 = option3Field.getText().trim();
        String opt4 = option4Field.getText().trim();
        int correct = (Integer) correctOptionBox.getSelectedItem();
        String diff = (String) difficultyBox.getSelectedItem();

        if (question.isEmpty() || opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty() || opt4.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (adminService.addQuestion(question, opt1, opt2, opt3, opt4, correct, diff)) {
            JOptionPane.showMessageDialog(this, "Question added successfully!");
            clearFields();
            loadQuestions();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add question. Check the database connection.");
        }
    }

    private void updateQuestion() {
        if (selectedQuestionId < 0) {
            JOptionPane.showMessageDialog(this, "Select a question from the table to update.");
            return;
        }

        String question = questionField.getText().trim();
        String opt1 = option1Field.getText().trim();
        String opt2 = option2Field.getText().trim();
        String opt3 = option3Field.getText().trim();
        String opt4 = option4Field.getText().trim();
        int correct = (Integer) correctOptionBox.getSelectedItem();
        String diff = (String) difficultyBox.getSelectedItem();

        if (question.isEmpty() || opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty() || opt4.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (adminService.updateQuestion(selectedQuestionId, question, opt1, opt2, opt3, opt4, correct, diff)) {
            JOptionPane.showMessageDialog(this, "Question updated successfully!");
            clearFields();
            selectedQuestionId = -1;
            loadQuestions();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update question. Check the database connection.");
        }
    }

    private void clearFields() {
        selectedQuestionId = -1;
        questionField.setText("");
        option1Field.setText("");
        option2Field.setText("");
        option3Field.setText("");
        option4Field.setText("");
        correctOptionBox.setSelectedIndex(0);
        difficultyBox.setSelectedIndex(0);
        questionTable.clearSelection();
    }
}
