package com.health.system.healthsystem.Controllers.Employee;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.util.Callback;
import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.ClientData;
import com.health.system.healthsystem.Models.Model;

public class ClientsController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<ClientData> clientsTable;
    @FXML
    private TableColumn<ClientData, String> usernameCol;
    @FXML
    private TableColumn<ClientData, String> emailCol;
    @FXML
    private TableColumn<ClientData, Double> weightCol;
    @FXML
    private TableColumn<ClientData, Double> heightCol;
    @FXML
    private TableColumn<ClientData, Double> BMICol;
    @FXML
    private TableColumn<ClientData, String> lastCol;
    @FXML
    private TableColumn<ClientData, Date> createdAtCol;
    @FXML
    private TableColumn<ClientData, Void> dashboardCol;
//    @FXML
//    private ListView<String> transactionList;

    private ObservableList<ClientData> clientsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        loadClients();
        setupSearch();
        //setupTableSelection();
    }

    private void initializeTable() {

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        heightCol.setCellValueFactory(new PropertyValueFactory<>("height"));
        BMICol.setCellValueFactory(new PropertyValueFactory<>("BMI"));
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        setupDashboardColumn();
        clientsTable.setItems(clientsList);
    }
    private void setupDashboardColumn() {
        dashboardCol.setCellFactory(new Callback<TableColumn<ClientData, Void>, TableCell<ClientData, Void>>() {
            @Override
            public TableCell<ClientData, Void> call(TableColumn<ClientData, Void> param) {
                return new TableCell<ClientData, Void>() {
                    private final Button btn = new Button("Dashboard");

                    {
                       // btn.setOnAction(event -> );
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    private void loadClients() {
        String trainerEmail =  Model.getInstance().getCurrentUserEmail();
        clientsList.clear();
        try (Connection conn = DatabaseConnection.connect()) {
            String idSql = """
                        SELECT user_id from users
                        WHERE  email= ?
                    """;
            PreparedStatement idstmt = conn.prepareStatement(idSql);
            idstmt.setString(1, trainerEmail);
            ResultSet idrs = idstmt.executeQuery();
            int trainerID = idrs.getInt("user_id");
            String sql = """
                        SELECT *
                        FROM users
                        WHERE trainerID = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ClientData client = new ClientData(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getDouble("weight"),
                        rs.getDouble("height"),
                        rs.getDate("updated_at"),
                        rs.getDate("created_at"));
                client.setBMI();
                clientsList.add(client);
            }
        } catch (SQLException e) {
            showAlert("Error loading clients: " + e.getMessage());
        }
    }

    private void setupSearch() {
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                filterClients(searchTerm);
            } else {
                loadClients();
            }
        });
    }

    private void filterClients(String searchTerm) {
        clientsList.clear();
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = """
                        SELECT u.username, u.email, u.created_at,
                               MAX(CASE WHEN a.account_type = 'CHECKING' THEN a.account_number END) as checking_account,
                               MAX(CASE WHEN a.account_type = 'CHECKING' THEN a.balance END) as checking_balance,
                               MAX(CASE WHEN a.account_type = 'SAVINGS' THEN a.account_number END) as savings_account,
                               MAX(CASE WHEN a.account_type = 'SAVINGS' THEN a.balance END) as savings_balance
                        FROM users u
                        LEFT JOIN accounts a ON u.user_id = a.customer_id
                        WHERE u.role = 'CLIENT' AND (u.email LIKE ? OR u.username LIKE ?)
                        GROUP BY u.user_id, u.username, u.email, u.created_at
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ClientData client = new ClientData(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getDouble("weight"),
                        rs.getDouble("height"),
                        rs.getDate("updated_at"),
                        rs.getDate("created_at"));
                client.setBMI();
                clientsList.add(client);
            }
        } catch (SQLException e) {
            showAlert("Error searching clients: " + e.getMessage());
        }
    }

//    private void setupTableSelection() {
//        clientsTable.getSelectionModel().selectedItemProperty().addListener(
//                (obs, oldSelection, newSelection) -> {
//                    if (newSelection != null) {
//                        loadClientTransactions(newSelection.getEmail());
//                    }
//                });
//    }

//    private void loadClientTransactions(String email) {
//        transactionList.getItems().clear();
//        try (Connection conn = DatabaseConnection.connect()) {
//            String sql = """
//                        SELECT t.transaction_date, t.transaction_type, t.amount, t.description,
//                               a.account_type, a.account_number
//                        FROM transactions t
//                        JOIN accounts a ON t.account_id = a.account_id
//                        JOIN users u ON a.customer_id = u.user_id
//                        WHERE u.email = ?
//                        ORDER BY t.transaction_date DESC
//                        LIMIT 20
//                    """;
//
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, email);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                String transaction = String.format("%s | %s | %s | $%.2f | %s",
//                        rs.getTimestamp("transaction_date"),
//                        rs.getString("account_type"),
//                        rs.getString("transaction_type"),
//                        rs.getDouble("amount"),
//                        rs.getString("description"));
//                transactionList.getItems().add(transaction);
//            }
//        } catch (SQLException e) {
//            showAlert("Error loading transactions: " + e.getMessage());
//        }
//    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}