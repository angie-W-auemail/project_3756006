package com.health.system.healthsystem.Controllers.Employee;


import com.health.system.healthsystem.Models.Model;
import com.health.system.healthsystem.Views.TrainerMenuOptions;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainerMenuController implements Initializable {
    @FXML
    private Button logout_btn;
    @FXML
    private Button profile_btn;
    @FXML private Button clients_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();

        Model.getInstance().getViewFactory().getTrainerSelectedMenuItem().set(TrainerMenuOptions.CLIENTS);

    }
    private void addListeners() {
        profile_btn.setOnAction(event -> onProfile());

        clients_btn.setOnAction(event -> onClients());

    }
    private void onClients() {
        Model.getInstance().getViewFactory().getTrainerSelectedMenuItem().set(TrainerMenuOptions.CLIENTS);
    }
    private void onProfile() {
        Model.getInstance().getViewFactory().getTrainerSelectedMenuItem().set(TrainerMenuOptions.PROFILE);
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}

