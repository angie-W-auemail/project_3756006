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

public class AddWaterController implements Initializable {

    @javafx.fxml.FXML
    private TextField water_volume;
    @javafx.fxml.FXML
    private Button record_water_btn;
    @javafx.fxml.FXML
    private TextField water_total;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadWaterData();
        record_water_btn.setOnAction(event -> handleAddWater());
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

    private void loadWaterData() {
        try (Connection conn = DatabaseConnection.connect()) {
            int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
            String sql = "SELECT waterintake FROM activities WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                water_total.setText(rs.getString("waterintake"));
            } else {
                water_total.setText("0");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to load total");
        }
    }

    private void handleAddWater() {
        try {
            double amount = Double.parseDouble(water_volume.getText().trim());
            processAddWater(amount);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }

    private void processAddWater(double amount) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                int newVolume = (int)((Double.parseDouble(water_total.getText())) + amount);
                int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
                String sql = "UPDATE activities SET waterintake = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newVolume);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                water_total.setText(String.valueOf(newVolume));
//                recordTransaction(conn, accountId, "WITHDRAWAL", -amount,
//                        String.format("Withdrawal from %s account", accountType));
                conn.commit();
                showAlert("Success", "Water entry recorded");

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


