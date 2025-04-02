package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Views.ClientMenuOptions;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;

public class DashboardController implements Initializable {

    public Text user_name;
    public Label signin_date;
    public ListView transaction_listview;
    public TextField message_fld;
    public Button send_message_btn;
    public Label exercise_today;
    public Label food_today;
    public Label water_today;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateDateTime();
        loadUserData();

        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
                    if (newValue == ClientMenuOptions.DASHBOARD) {
                        loadUserData();
                    }
                });

        //send_message_btn.setOnAction(event -> handleSendMessage());
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        signin_date.setText(now.format(formatter));
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.connect()) {
            int userId = getUserId(conn, Model.getInstance().getCurrentUserEmail());
            String sql = "SELECT username FROM users WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getCurrentUserEmail());
            ResultSet rs = pstmt.executeQuery();

            String nsql = "SELECT duration, calorie, waterintake FROM activities WHERE user_id = ?";
            PreparedStatement nstmt = conn.prepareStatement(nsql);
            nstmt.setInt(1, userId);
            ResultSet ns =nstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                if (username != null && !username.isEmpty()) {
                    user_name.setText("Hi, " + username);
                } else {
                    user_name.setText("Hello, Client");
                }
            }

            if (ns.next()) {
                exercise_today.setText((ns.getString("duration")) + " mins");
                food_today.setText((ns.getString("calorie")) + " cals");
                water_today.setText((ns.getString("waterintake")) + " ml.s");
            } else {
                exercise_today.setText("0 mins");
                food_today.setText("0 cals");
                water_today.setText("0 ml.s");
            }
        } catch (Exception e) {
            e.printStackTrace();
            user_name.setText("Hello, Client");
        }
    }

    private String getCurrentUserEmail() {
        return Model.getInstance().getCurrentUserEmail();
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

    private void recordTransaction(Connection conn, int accountId, String type, double amount, String description)
            throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
