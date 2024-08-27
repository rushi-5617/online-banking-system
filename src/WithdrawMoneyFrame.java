import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class WithdrawMoneyFrame extends JFrame {
    JTextField amountField;
    JPasswordField pinField;
    JButton withdrawButton;
    DatabaseConnection dbConnection;
    String accountId;

    public WithdrawMoneyFrame(DatabaseConnection dbConnection, String accountId) {
        this.dbConnection = dbConnection;
        this.accountId = accountId;

        setTitle("Withdraw Money");
        setSize(400, 250);
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

        withdrawButton = new JButton("Withdraw");
        withdrawButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        withdrawButton.setBackground(Color.decode("#FF0000"));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setPreferredSize(new Dimension(150, 40));

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
        panel.add(withdrawButton, gbc);

        add(panel);

        withdrawButton.addActionListener(e -> processWithdrawal());

        setVisible(true);
    }

    private void processWithdrawal() {
        try {
            String amountText = amountField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();

            if (amountText.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in both amount and PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dbConnection.verifyUser(accountId, pin)) {
                double currentBalance = getCurrentBalance();

                if (currentBalance >= amount) {
                    Connection conn = dbConnection.getConnection();
                    conn.setAutoCommit(false);

                    try {
                        String updateBalanceSql = "UPDATE user_accounts SET balance = balance - ? WHERE account_id = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                            pstmt.setDouble(1, amount);
                            pstmt.setString(2, accountId);

                            int rowsAffected = pstmt.executeUpdate();
                            if (rowsAffected <= 0) {
                                conn.rollback();
                                JOptionPane.showMessageDialog(this, "Failed to withdraw money.", "Error", JOptionPane.ERROR_MESSAGE);
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
                            pstmt.setString(5, "Debit");
                            pstmt.setDouble(6, currentBalance - amount);

                            pstmt.executeUpdate();
                        }

                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Money debited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (SQLException e) {
                        conn.rollback();
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        conn.setAutoCommit(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private double getCurrentBalance() throws SQLException {
        String sql = "SELECT balance FROM user_accounts WHERE account_id = ?";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                throw new SQLException("Account not found.");
            }
        }
    }
}

