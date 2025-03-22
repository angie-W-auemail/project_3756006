package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private TextField role_fld;
    @FXML private TextField weight_fld;
    @FXML private TextField height_fld;
    @FXML private TextField emailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private RadioButton male_radio;
    @FXML private RadioButton female_radio;
    @FXML private ToggleGroup genderGroup;
    @FXML private ComboBox<String> trainer_selector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT username, email, weight, height, gender,  trainerID, role FROM users WHERE email = ?";
            String sql_trainers = "SELECT email from users where role = 'Trainer'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement tstmt = conn.prepareStatement(sql_trainers);
            pstmt.setString(1, Model.getInstance().getCurrentUserEmail());
            ResultSet rs = pstmt.executeQuery();
            ResultSet ts=tstmt.executeQuery();

            if (rs.next()) {

                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
                role_fld.setText(rs.getString("role"));
                weight_fld.setText(rs.getString("weight"));
                height_fld.setText(rs.getString("height"));
                genderGroup = new ToggleGroup();
                male_radio.setToggleGroup(genderGroup);
                female_radio.setToggleGroup(genderGroup);
                if (rs.getString("gender").equals("Male")) {
                    male_radio.setSelected(true);
                }
                else{
                    female_radio.setSelected(true);
                }
                ObservableList<String> trainers = FXCollections.observableArrayList();
                while (ts.next()) {
                    trainers.add(ts.getString("username"));
                }
                trainer_selector.setItems(trainers);

            }
        } catch (SQLException e) {
            showMessage("Error loading user data", false);
        }
    }

    @FXML
    private void handleUpdateDetails() {
        String newUsername = usernameField.getText().trim();
        
        if (newUsername.isEmpty()) {
            showMessage("Username cannot be empty", false);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "UPDATE users SET username = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUsername);
            pstmt.setString(2, Model.getInstance().getCurrentUserEmail());
            pstmt.executeUpdate();
            
            showMessage("Profile updated successfully", true);
        } catch (SQLException e) {
            showMessage("Error updating profile", false);
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("All password fields are required", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("New passwords do not match", false);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            if (validateCurrentPassword(conn, currentPassword)) {
                updatePassword(conn, newPassword);
                showMessage("Password changed successfully", true);
                clearPasswordFields();
            } else {
                showMessage("Current password is incorrect", false);
            }
        } catch (SQLException e) {
            showMessage("Error changing password", false);
        }
    }

    private boolean validateCurrentPassword(Connection conn, String currentPassword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Model.getInstance().getCurrentUserEmail());
        pstmt.setString(2, currentPassword);
        ResultSet rs = pstmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private void updatePassword(Connection conn, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newPassword);
        pstmt.setString(2, Model.getInstance().getCurrentUserEmail());
        pstmt.executeUpdate();
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add(isSuccess ? "success-message" : "error-message");
    }
}