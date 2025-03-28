package com.health.system.healthsystem.Controllers.Client;

import com.health.system.healthsystem.Views.ClientMenuOptions;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.health.system.healthsystem.Models.DatabaseConnection;
import com.health.system.healthsystem.Models.Model;

public class DashboardController implements Initializable {

    public Text user_name;
    public Label signin_date;
    public Label checking_bal;
//    public Label savings_bal;
    public ListView transaction_listview;
    //public TextField message_fld;
    public Button send_money_btn;

    private String currentUserEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        loadAccountData();

        updateDateTime();

        loadUserData();

        loadTransactionHistory();
        loadIncomeExpenseSummary();
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
                    if (newValue == ClientMenuOptions.DASHBOARD) {
//                        loadAccountData();
                        loadIncomeExpenseSummary();

                    }
                });

        //send_money_btn.setOnAction(event -> handleSendMoney());
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        signin_date.setText("Today, " + now.format(formatter));
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT username FROM users WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getCurrentUserEmail());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                if (username != null && !username.isEmpty()) {
                    user_name.setText("Hi, " + username);
                } else {
                    user_name.setText("Hello, Client");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            user_name.setText("Hello, Client");
        }
    }

//    private void loadAccountData() {
//        try (Connection conn = DatabaseConnection.connect()) {
//            String currentEmail = getCurrentUserEmail();
//
//            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
//            PreparedStatement userStmt = conn.prepareStatement(userIdSql);
//            userStmt.setString(1, currentEmail);
//            ResultSet userRs = userStmt.executeQuery();
//
//            if (!userRs.next()) {
//                return;
//            }
//
//            int userId = userRs.getInt("user_id");
//
//            String sql = "SELECT account_type, account_number, balance, created_at FROM accounts WHERE customer_id = ?";
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            pstmt.setInt(1, userId);
//            ResultSet rs = pstmt.executeQuery();
//
//            checking_bal.setText("No Account");
//            savings_bal.setText("No Account");
//
//            while (rs.next()) {
//                String accountType = rs.getString("account_type");
//                String accountNumber = rs.getString("account_number");
//                double balance = rs.getDouble("balance");
//
//                if ("CHECKING".equals(accountType)) {
//                    checking_bal.setText(String.format("$%.2f", balance));
//                } else if ("SAVINGS".equals(accountType)) {
//                    savings_bal.setText(String.format("$%.2f", balance));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            checking_bal.setText("Error");
//            savings_bal.setText("Error");
//        }
//    }

    private String getCurrentUserEmail() {
        return Model.getInstance().getCurrentUserEmail();
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }

    private void loadIncomeExpenseSummary(Connection conn) throws SQLException {
        String userIdSql = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement userStmt = conn.prepareStatement(userIdSql);
        userStmt.setString(1, getCurrentUserEmail());
        ResultSet userRs = userStmt.executeQuery();

        if (!userRs.next()) {
            return;
        }
        int userId = userRs.getInt("user_id");

        String incomeSql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.account_id " +
                "WHERE a.customer_id = ? AND t.transaction_type IN ('DEPOSIT', 'RECEIVE')";
        PreparedStatement incomeStmt = conn.prepareStatement(incomeSql);
        incomeStmt.setInt(1, userId);
        ResultSet incomeRs = incomeStmt.executeQuery();

        if (incomeRs.next()) {
            double income = incomeRs.getDouble("total");

        }

        String expenseSql = "SELECT COALESCE(SUM(ABS(amount)), 0) as total FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.account_id " +
                "WHERE a.customer_id = ? AND t.transaction_type IN ('WITHDRAWAL', 'SEND')";
        PreparedStatement expenseStmt = conn.prepareStatement(expenseSql);
        expenseStmt.setInt(1, userId);
        ResultSet expenseRs = expenseStmt.executeQuery();

        if (expenseRs.next()) {
            double expense = expenseRs.getDouble("total");

        }
    }

    private void loadIncomeExpenseSummary() {
        try (Connection conn = DatabaseConnection.connect()) {
            loadIncomeExpenseSummary(conn);
        } catch (SQLException e) {
            e.printStackTrace();


        }
    }

    private void loadTransactionHistory() {
        try (Connection conn = DatabaseConnection.connect()) {
            loadTransactionHistory(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            transaction_listview.getItems().clear();
        }
    }

    private void loadTransactionHistory(Connection conn) throws SQLException {
        String userIdSql = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement userStmt = conn.prepareStatement(userIdSql);
        userStmt.setString(1, getCurrentUserEmail());
        ResultSet userRs = userStmt.executeQuery();

        if (!userRs.next()) {
            return;
        }
        int userId = userRs.getInt("user_id");

        String sql = "SELECT t.transaction_date, t.transaction_type, t.amount, t.description, " +
                "a.account_type, a.account_number " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.account_id " +
                "WHERE a.customer_id = ? " +
                "ORDER BY t.transaction_date DESC LIMIT 10";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();

        transaction_listview.getItems().clear();

        while (rs.next()) {
            String date = rs.getTimestamp("transaction_date").toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String type = rs.getString("transaction_type");
            double amount = rs.getDouble("amount");
            String description = rs.getString("description");
            String accountType = rs.getString("account_type");
            String accountNumber = rs.getString("account_number");

            String transactionText = String.format("%s | %s | %s | $%.2f | %s | Account: %s",
                    date, type, description, amount,
                    accountType, accountNumber.substring(accountNumber.length() - 4));

            transaction_listview.getItems().add(transactionText);
        }
    }

    @FXML

    private boolean validateReceiver(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND role = 'CLIENT'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    private int getUserId(Connection conn, String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
            throw new SQLException("User not found");
        }
    }

    private int getCheckingAccountId(Connection conn, int userId) throws SQLException {
        String sql = "SELECT account_id FROM accounts WHERE customer_id = ? AND account_type = 'CHECKING'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("account_id");
            }
            throw new SQLException("Checking account not found");
        }
    }

    private double getAccountBalance(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
            throw new SQLException("Account not found");
        }
    }

    private void updateBalance(Connection conn, int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        }
    }

    private void recordTransaction(Connection conn, int accountId, String type, double amount, String description)
            throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
