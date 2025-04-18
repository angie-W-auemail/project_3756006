package com.health.system.healthsystem.Controllers;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;
import com.health.system.healthsystem.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SigninController implements Initializable {

    public ChoiceBox<AccountType> account_selector;
    public Label email_lbl;
    public TextField email_address_fld;
    public TextField password_fld;
    public Button signin_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        account_selector.setItems(
                FXCollections.observableArrayList(AccountType.Trainee, AccountType.Admin, AccountType.Trainer));
        account_selector.setValue(Model.getInstance().getViewFactory().getSigninAccountType());
        account_selector.valueProperty().addListener(
                (observable -> Model.getInstance().getViewFactory().setSigninAccountType(account_selector.getValue())));
        signin_btn.setOnAction(e -> {
            onSigninButtonClick();
        });

    }

    private void onSigninButtonClick() {
        String email = email_address_fld.getText().trim();
        String password = password_fld.getText().trim();
        AccountType selectedRole = account_selector.getValue();
    
        if (email.isEmpty() || password.isEmpty()) {
            error_lbl.setText("email and password cannot be empty!");
            return;
        }
    
        if (validateUser(email, password, selectedRole)) {

            Model.getInstance().setCurrentUserEmail(email);

            Stage stage = (Stage) error_lbl.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);

            switch (selectedRole) {
                case Trainee:
                    Model.getInstance().getViewFactory().showClientWindow();
                    break;
                case Trainer:
                    Model.getInstance().getViewFactory().showTrainerWindow();
                    break;
                case Admin:
                    Model.getInstance().getViewFactory().showAdminWindow();
                    break;
            }
        } else {
            error_lbl.setText("invalid email or password, or wrong account type selected!");
        }
    }
    private boolean validateUser(String email, String password, AccountType expectedRole) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ? AND role = ?";
        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, expectedRole.toString());
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleSignUp() {
        loadFXML("/Fxml/Signup.fxml");
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
        }
    }
}
