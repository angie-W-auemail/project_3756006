package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddExerciseController implements Initializable {
    @javafx.fxml.FXML
    private TextField exercise_total;
    @javafx.fxml.FXML
    private Button record_exercise_btn;
    @javafx.fxml.FXML
    private TextField exercise_minutes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExerciseData();
        record_exercise_btn.setOnAction(event -> handleAddExercise());
    }

    private int getUserId(Connection conn, String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
            throw new SQLException("User not found");
        }
    }

    private void loadExerciseData() {
        try (Connection conn = DatabaseConnection.connect()) {
            int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
            String sql = "SELECT duration FROM activities WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                exercise_total.setText(rs.getString("duration"));
            } else {
                exercise_total.setText("0");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to load total");
        }
    }

    private void handleAddExercise() {
        try {
            double amount = Double.parseDouble(exercise_minutes.getText().trim());
            processAddExercise(amount);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }

    private void processAddExercise(double amount) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                int newDuration = (int) ((Integer.parseInt(exercise_total.getText())) + amount);
                int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
                String sql = "UPDATE activities SET duration = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newDuration);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                exercise_total.setText(String.valueOf(newDuration));
//                recordTransaction(conn, accountId, "WITHDRAWAL", -amount,
//                        String.format("Withdrawal from %s account", accountType));
                conn.commit();
                showAlert("Success", "Exercise entry recorded");

            } catch (Exception e) {
                conn.rollback();
                showAlert("Error", "Record entry failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            showAlert("Error", "Database connection failed");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

//    private void recordTransaction(Connection conn, int accountId, String type, double amount, String description) throws SQLException {
//        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description, transaction_date) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, accountId);
//            pstmt.setString(2, type);
//            pstmt.setDouble(3, amount);
//            pstmt.setString(4, description);
//            pstmt.executeUpdate();
//        }
//    }

}


