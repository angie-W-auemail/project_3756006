package com.health.system.healthsystem.Models;

import javafx.beans.property.*;
import java.sql.Date;

public class ClientData {
    private final StringProperty username;
    private final StringProperty email;
    private final DoubleProperty weight;
    private final DoubleProperty height;
    private double BMI=0;
    private final ObjectProperty<Date> lastUpdate;
    private final ObjectProperty<Date> createdAt;

    public ClientData(String username, String email, double weight,
                     double height, Date lastUpdate,
                     Date createdAt) {
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.weight = new SimpleDoubleProperty(weight);
        this.height = new SimpleDoubleProperty(height);
        this.lastUpdate = new SimpleObjectProperty<>(lastUpdate);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }
    public void setBMI(){
        double value =this.getWeight()/this.getHeight()/this.getHeight()*10000;
        this.BMI=Math.round(value * 100.0) / 100.0;}

    // Getters
    public String getUsername() { return username.get(); }
    public String getEmail() { return email.get(); }
    public double getWeight() { return weight.get(); }
    public double getHeight() { return height.get(); }
    public double getBMI() { return this.BMI; }
    public Date getLastUpdate() { return lastUpdate.get(); }
    public Date getCreatedAt() { return createdAt.get(); }


    

    // Property getters
    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public DoubleProperty weightProperty() { return weight; }
    public DoubleProperty heightProperty() { return height; }
    public ObjectProperty<Date> lastUpdateProperty() { return lastUpdate; }
    public ObjectProperty<Date> createdAtProperty() { return createdAt; }
}