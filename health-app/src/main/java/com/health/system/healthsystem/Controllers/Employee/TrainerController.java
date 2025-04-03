
package com.health.system.healthsystem.Controllers.Employee;

import com.health.system.healthsystem.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainerController implements Initializable {
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getTrainerSelectedMenuItem().addListener((observableValue, oldValue, newValue) -> {
            switch(newValue) {
                case PROFILE -> {
                    Model.getInstance().getViewFactory().setProfileView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                }
                case SUMMARY -> {
                    Model.getInstance().getViewFactory().setSummaryView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getSummaryView());
                }
                case CLIENTS -> {
                    Model.getInstance().getViewFactory().setClientsView(null);
                    client_parent.setCenter(Model.getInstance().getViewFactory().getClientsView());
                }
            }
        });
    }
}