package com.health.system.healthsystem;

import com.health.system.healthsystem.Models.DatabaseInitializer;
import com.health.system.healthsystem.Models.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class HealthApplication extends Application {

    @Override
    public void init() {
        try {
            DatabaseInitializer.initialize();
            System.out.println("Database initialization completed");
        } catch (Exception e) {
            System.err.println("Failed to initialize database");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage stage) {
        Model.getInstance().getViewFactory().showSinginWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}