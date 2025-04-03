package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Models.Activity;
import com.health.system.healthsystem.Models.ClientData;
import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import java.sql.Date;
import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;


public class SummaryController implements Initializable {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button applyDateRangeBtn;
    @FXML
    private TableView<Activity> activityTable;
    @FXML private TableColumn<Activity, Date> datetimeCol;
    @FXML private TableColumn<Activity, String> activityCol;
    @FXML private TableColumn<Activity, Integer> calorieCol;
    @FXML private TableColumn<Activity, Integer> waterCol;
    @FXML private TableColumn<Activity, Double> weightCol;
    @FXML private TableColumn<Activity, Integer> durationCol;
    @FXML private Label avgCaloriesLabel;
    @FXML private Label totalWaterLabel;
    @FXML private Label avgWeightLabel;
    ClientData client = Model.getInstance().getCurrentClient();
    int user_id;
    private ObservableList<Activity> activities = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        initializeTable();
        loadClient();
        filterActivities();
        applyDateRangeBtn.setOnAction(e -> filterActivities());
        update();
    }

    private void initializeTable() {

        datetimeCol.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activity"));
        calorieCol.setCellValueFactory(new PropertyValueFactory<>("calorie"));
        waterCol.setCellValueFactory(new PropertyValueFactory<>("waterIntake"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        endDatePicker.setValue(today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));

    }
    public  void filterActivities() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) return;

        activityTable.setItems(activities.filtered(activity -> {
            LocalDate activityDate = activity.getInput_datetime().toLocalDate();
            return !activityDate.isBefore(startDate) && !activityDate.isAfter(endDate);
        }));

    }
    public void update() {

    }
    private void loadClient() {

        try (Connection conn = DatabaseConnection.connect()) {
            String idsql = """
                        SELECT user_id
                        FROM users
                        WHERE email = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(idsql);
            pstmt.setString(1, client.getEmail());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                user_id = rs.getInt("user_id");
            }
            String activitySql = """
                        SELECT *
                        FROM activities
                        WHERE user_id = ?
                    """;
            PreparedStatement activityStmt = conn.prepareStatement(activitySql);
            activityStmt.setInt(1, user_id);
            ResultSet rsActivity = activityStmt.executeQuery();
            while (rsActivity.next()) {
                Activity inputActivity = new Activity(
                        rsActivity.getString("Activity"),
                        rsActivity.getInt("calorie"),
                        rsActivity.getInt("waterintake"),
                        rsActivity.getDouble("weight"),
                        rsActivity.getInt("duration"),
                        rsActivity.getDate("input_date"));
                activities.add(inputActivity);

            }
            activityTable.setItems(activities);

        } catch (SQLException e) {
            showAlert("Error loading clients: " + e.getMessage());
        }
    }



    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}