package com.health.system.healthsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HealthController {
    public VBox rootPane;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onBankButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}