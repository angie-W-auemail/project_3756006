package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Models.Model;
import com.health.system.healthsystem.Views.ClientMenuOptions;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button dashboard_btn;
    public Button water_btn;
    public Button food_btn;
    public Button exercise_btn;
    public Button profile_btn;
    public Button logout_btn;
    //public Button report_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }

    private void addListeners() {
        dashboard_btn.setOnAction(e -> {
            onDashboard();
        });
        water_btn.setOnAction(e -> {
            onWater();
        });
        food_btn.setOnAction(e -> {
            onFood();
        });
        exercise_btn.setOnAction(e -> {
            onExercise();
        });
        profile_btn.setOnAction(e -> {
            onProfile();
        });
    }

    private void onDashboard() {
        System.out.println("Dashboard button clicked");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.DASHBOARD);
    }

    private void onProfile() {
        System.out.println("Profile");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.PROFILE);
    }

    private void onWater() {
        System.out.println("Add water");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.ADDWATER);
    }

    private void onFood() {
        System.out.println("Add food");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.ADDFOOD);
    }

    private void onExercise() {
        System.out.println("Add exercise");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.ADDEXERCISE);
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
