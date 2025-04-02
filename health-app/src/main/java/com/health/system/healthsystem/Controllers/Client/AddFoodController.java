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

public class AddFoodController implements Initializable {

    @javafx.fxml.FXML
    public TextField food_type;
    @javafx.fxml.FXML
    public TextField food_calories;
    @javafx.fxml.FXML
    private Button record_food_btn;
    @javafx.fxml.FXML
    private TextField food_total;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFoodData();
        record_food_btn.setOnAction(event -> handleAddFood());
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

    private void loadFoodData() {
        try (Connection conn = DatabaseConnection.connect()) {
            int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
            String sql = "SELECT calorie FROM activities WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                food_total.setText(rs.getString("calorie"));
            } else {
                food_total.setText("0");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to load total");
        }
    }

    private void handleAddFood() {
        TextField typeField = food_type;
        try {
            double amount = Double.parseDouble(food_calories.getText().trim());
            String type = typeField.getText();
            processAddFood(type, amount);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }

    private void processAddFood(String type, double amount) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                int newCalorie = (int)((Double.parseDouble(food_total.getText())) + amount);
                int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
                String sql = "UPDATE activities SET calorie = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newCalorie);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                food_total.setText(String.valueOf(newCalorie));
//                recordTransaction(conn, accountId, "WITHDRAWAL", -amount,
//                        String.format("Withdrawal from %s account", accountType));
                conn.commit();
                showAlert("Success", "Food entry recorded");

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
