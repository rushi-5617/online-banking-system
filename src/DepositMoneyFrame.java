import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class DepositMoneyFrame extends JFrame {
    JTextField amountField;
    JPasswordField pinField;
    JButton depositButton;
    DatabaseConnection dbConnection;
    String accountId;

    public DepositMoneyFrame(DatabaseConnection dbConnection, String accountId) {
        this.dbConnection = dbConnection;
        this.accountId = accountId;

        setTitle("Deposit Money");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#17153B"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        amountField = new JTextField(10);
        amountField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        pinField = new JPasswordField(10);
        pinField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        depositButton = new JButton("Deposit");
        depositButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        depositButton.setPreferredSize(new Dimension(150, 40));
        depositButton.setBackground(Color.decode("#FF0000"));
        depositButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(amountLabel, gbc);

        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(pinLabel, gbc);

        gbc.gridx = 1;
        panel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(depositButton, gbc);

        add(panel);

        depositButton.addActionListener(e -> processDeposit());

        setVisible(true);
    }

    private void processDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String pin = new String(pinField.getPassword());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dbConnection.verifyUser(accountId, pin)) {
                Connection conn = dbConnection.getConnection();
                conn.setAutoCommit(false);

                try {
                    String updateBalanceSql = "UPDATE user_accounts SET balance = balance + ? WHERE account_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                        pstmt.setDouble(1, amount);
                        pstmt.setString(2, accountId);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected <= 0) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Failed to deposit money.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    String getBalanceSql = "SELECT balance FROM user_accounts WHERE account_id = ?";
                    double newBalance;
                    try (PreparedStatement pstmt = conn.prepareStatement(getBalanceSql)) {
                        pstmt.setString(1, accountId);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            newBalance = rs.getDouble("balance");
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Failed to retrieve balance.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    String transactionId = dbConnection.generateTransactionId();
                    String recordTransactionSql = "INSERT INTO transactions (transaction_id, from_user_id, to_user_id, transaction_date, amount, transaction_type, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(recordTransactionSql)) {
                        pstmt.setString(1, transactionId);
                        pstmt.setString(2, accountId);
                        pstmt.setString(3, accountId);
                        pstmt.setDouble(4, amount);
                        pstmt.setString(5, "Credit");
                        pstmt.setDouble(6, newBalance);
                        pstmt.executeUpdate();
                    }

                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Money credited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (SQLException e) {
                    conn.rollback();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    conn.setAutoCommit(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
