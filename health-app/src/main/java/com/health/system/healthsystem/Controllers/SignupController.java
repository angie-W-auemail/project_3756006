package com.health.system.healthsystem.Controllers;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Views.AccountType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.sql.Statement;

public class SignupController implements Initializable {
    @FXML
    private ChoiceBox<AccountType> account_selector;
    @FXML
    private TextField email_address_fld;
    @FXML
    private TextField username_fld;
    @FXML
    private TextField password_fld;
    @FXML
    private Label error_lbl;
    @FXML
    private ComboBox<String> gender_selector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        account_selector.getItems().addAll(AccountType.Trainee, AccountType.Admin, AccountType.Trainer);
        account_selector.setValue(AccountType.Trainee);

        if (gender_selector != null) {
            if (gender_selector.getItems().isEmpty()) {
                gender_selector.getItems().addAll("Male", "Female");
            }
            gender_selector.setValue("Male");
        }
    }

    public void handleSignIn(ActionEvent event) {
        loadFXML("/Fxml/Signin.fxml", event);
    }

    private void loadFXML(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp() {
        String email = email_address_fld.getText().trim();
        String username = username_fld.getText().trim();
        String password = password_fld.getText().trim();
        AccountType accountType = account_selector.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            error_lbl.setText("email and password cannot be empty!");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            if (isEmailExists(conn, email)) {
                error_lbl.setText("email already exists!");
                return;
            }

            conn.setAutoCommit(false);
            try {
                insertUser(conn, email, password, username, accountType);
                conn.commit();
                showSuccessAndRedirect();
            } catch (SQLException e) {
                conn.rollback();
                error_lbl.setText("signup failed: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            error_lbl.setText("database connection failed!");
            e.printStackTrace();
        }
    }

    private boolean isEmailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    private int insertUser(Connection conn, String email, String password, String username, AccountType accountType)
            throws SQLException {
        String sql = "INSERT INTO users (email, password, username, role, gender, created_at) VALUES (?, ?, ?, ?, ?, datetime('now'))";
        String sqlA = "INSERT INTO activities (user_id, activity, calorie, waterintake, duration) VALUES (?, ?, 0, 0, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, username.isEmpty() ? "New User" : username);
            pstmt.setString(4, accountType.toString());

            String gender = "Male";
            if (gender_selector != null && gender_selector.getValue() != null) {
                gender = gender_selector.getValue();
            }
            pstmt.setString(5, gender);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                PreparedStatement pstmtA = conn.prepareStatement(sqlA);
                pstmtA.setInt(1, rs.getInt(1));
                pstmtA.setString(2, "activities");
                pstmtA.executeUpdate();
                return rs.getInt(1);
            }

            throw new SQLException("create user failed, no ID obtained");
        }
    }

    private void showSuccessAndRedirect() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("signup success");
        alert.setHeaderText(null);
        alert.setContentText("account created successfully! please login.");
        alert.showAndWait();

        loadFXML("/Fxml/Signin.fxml");
    }

    public void handleSignIn() {
        loadFXML("/Fxml/Signin.fxml");
    }

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) email_address_fld.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            error_lbl.setText("page loading failed!");
        }
    }

}
