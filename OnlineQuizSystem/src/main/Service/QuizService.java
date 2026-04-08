package service;

import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class QuizService {

    public void startQuiz(String difficulty) {
        int score = 0, correct = 0, wrong = 0, attempted = 0;

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM questions WHERE difficulty=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, difficulty);

            ResultSet rs = ps.executeQuery();
            Scanner sc = new Scanner(System.in);

            while (rs.next()) {
                attempted++;
                System.out.println("\n" + rs.getString("question_text"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));

                System.out.print("Your answer: ");
                int ans = sc.nextInt();

                if (ans == rs.getInt("correct_option")) {
                    score += 5;
                    correct++;
                } else {
                    wrong++;
                }
            }

            System.out.println("\n===== RESULT =====");
            System.out.println("Score: " + score);
            System.out.println("Attempted: " + attempted);
            System.out.println("Correct: " + correct);
            System.out.println("Wrong: " + wrong);
            System.out.println("\n\"Success is the sum of small efforts repeated daily.\"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveResult(String username, int score, int attempted, int correct, int wrong) {
        String userQuery = "SELECT id FROM users WHERE username=?";
        String insertQuery = "INSERT INTO quiz_results (user_id, score, attempted, correct, wrong) VALUES (?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(userQuery)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    try (PreparedStatement ps2 = con.prepareStatement(insertQuery)) {
                        ps2.setInt(1, userId);
                        ps2.setInt(2, score);
                        ps2.setInt(3, attempted);
                        ps2.setInt(4, correct);
                        ps2.setInt(5, wrong);
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
