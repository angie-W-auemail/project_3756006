package com.health.system.healthsystem.Models;
import javafx.beans.property.*;
import java.sql.Date;
public class Activity {
    private final StringProperty activity ;
    private final IntegerProperty calorie;
    private final IntegerProperty  water_intake;
    private final DoubleProperty weight;
    private final IntegerProperty duration;
    private ObjectProperty<Date> input_datetime;


    public Activity(String activity, int calorie, int water_intake, double weight,
                     int duration, Date input_datetime) {
        this.activity = new SimpleStringProperty(activity);
        this.calorie = new SimpleIntegerProperty(calorie);
        this.water_intake = new SimpleIntegerProperty(water_intake);
        this.weight = new SimpleDoubleProperty(weight);
        this.duration = new SimpleIntegerProperty(duration);
        this.input_datetime = new SimpleObjectProperty<>(input_datetime);
    }

    public String getActivity() {return activity.get();}
    public int getCalorie() {return calorie.get();}
    public int getWater_intake() {return water_intake.get();}
    public double getWeight() {return weight.get();}
    public int getDuration() {return duration.get();}
    public Date getInput_datetime() {return input_datetime.get();}
    public StringProperty activityProperty() { return activity; }
    public IntegerProperty calorieProperty() { return calorie; }
    public IntegerProperty water_intakeProperty() { return water_intake; }
    public DoubleProperty weightProperty() { return weight; }
    public IntegerProperty durationProperty() { return duration; }
    public ObjectProperty<Date> input_datetimeProperty() { return input_datetime; }

}
