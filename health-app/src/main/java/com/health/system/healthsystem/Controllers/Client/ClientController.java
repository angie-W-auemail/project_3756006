package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {


    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observableValue, oldValue, newValue) -> {
            switch(newValue) {
                case PROFILE -> {
                    Model.getInstance().getViewFactory().setProfileView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                }
                case ADDFOOD -> {
                    Model.getInstance().getViewFactory().setAddFoodView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getAddFoodView());
                }
                case ADDWATER -> {
                    Model.getInstance().getViewFactory().setAddWaterView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getAddWaterView());
                }
                case ADDEXERCISE -> {
                    Model.getInstance().getViewFactory().setAddExerciseView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getAddExerciseView());
                }
                case TRANSACTIONS -> {
                    Model.getInstance().getViewFactory().setTransactionsView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getTransactionsView());
                }
                case DASHBOARD -> {
                    Model.getInstance().getViewFactory().setDashboardView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
                }
                case ACCOUNTS -> {
                    Model.getInstance().getViewFactory().setAccountsView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getAccountsView());
                }
            }
        });
    }

}
