package service;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public boolean addQuestion(String question, String opt1, String opt2, String opt3, String opt4, int correct, String diff) {
        String query = "INSERT INTO questions(question_text, option1, option2, option3, option4, correct_option, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, question);
            ps.setString(2, opt1);
            ps.setString(3, opt2);
            ps.setString(4, opt3);
            ps.setString(5, opt4);
            ps.setInt(6, correct);
            ps.setString(7, diff);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQuestion(int id, String question, String opt1, String opt2, String opt3, String opt4, int correct, String diff) {
        String query = "UPDATE questions SET question_text=?, option1=?, option2=?, option3=?, option4=?, correct_option=?, difficulty=? WHERE id=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, question);
            ps.setString(2, opt1);
            ps.setString(3, opt2);
            ps.setString(4, opt3);
            ps.setString(5, opt4);
            ps.setInt(6, correct);
            ps.setString(7, diff);
            ps.setInt(8, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<QuestionData> getAllQuestions() {
        List<QuestionData> questions = new ArrayList<>();
        String query = "SELECT * FROM questions ORDER BY id DESC LIMIT 30";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return questions;
        }
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                questions.add(new QuestionData(
                    rs.getInt("id"),
                    rs.getString("question_text"),
                    rs.getString("option1"),
                    rs.getString("option2"),
                    rs.getString("option3"),
                    rs.getString("option4"),
                    rs.getInt("correct_option"),
                    rs.getString("difficulty")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
