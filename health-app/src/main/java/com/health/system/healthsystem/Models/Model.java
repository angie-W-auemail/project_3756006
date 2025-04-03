package com.health.system.healthsystem.Models;

import com.health.system.healthsystem.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private String currentUserEmail;
    private ClientData currentClient;

    private Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }
    public void setCurrentClient(ClientData client) {this.currentClient = client;}
    public ClientData getCurrentClient() {return this.currentClient;}
}
