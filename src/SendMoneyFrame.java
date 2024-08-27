import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class SendMoneyFrame extends JFrame {
    private DatabaseConnection dbConnection;
    private String userAccountId;

    public SendMoneyFrame(DatabaseConnection dbConnection, String userAccountId) {
        this.dbConnection = dbConnection;
        this.userAccountId = userAccountId;

        setTitle("Send Money");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#17153B"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel recipientIdLabel = new JLabel("Recipient Account ID:");
        recipientIdLabel.setForeground(Color.WHITE);
        recipientIdLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        JTextField recipientIdField = new JTextField(10);
        recipientIdField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        JPasswordField pinField = new JPasswordField(10);
        pinField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(90, 30));
        sendButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        sendButton.setBackground(Color.decode("#FF0000"));
        sendButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(recipientIdLabel, gbc);

        gbc.gridx = 1;
        panel.add(recipientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(amountLabel, gbc);

        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(pinLabel, gbc);

        gbc.gridx = 1;
        panel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(sendButton, gbc);

        add(panel);

        sendButton.addActionListener(e -> {
            String recipientId = recipientIdField.getText();
            String amountText = amountField.getText();
            String pin = new String(pinField.getPassword());
        
            if (recipientId.isEmpty() || amountText.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(SendMoneyFrame.this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(SendMoneyFrame.this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = processTransaction(recipientId, amount, pin);
        
                if (success) {
                    JOptionPane.showMessageDialog(SendMoneyFrame.this, "Money sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(SendMoneyFrame.this, "Failed to send money. Please check your details and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SendMoneyFrame.this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private boolean processTransaction(String recipientId, double amount, String pin) {
        if (!dbConnection.verifyUser(userAccountId, pin)) {
            JOptionPane.showMessageDialog(this, "Invalid PIN. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);
            
            String getBalanceSql = "SELECT balance FROM user_accounts WHERE account_id = ?";
            double currentBalance;
            try (PreparedStatement balanceStmt = conn.prepareStatement(getBalanceSql)) {
                balanceStmt.setString(1, userAccountId);
                ResultSet rs = balanceStmt.executeQuery();
                if (rs.next()) {
                    currentBalance = rs.getDouble("balance");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            if (currentBalance < amount) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String deductSql = "UPDATE user_accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement deductStmt = conn.prepareStatement(deductSql)) {
                deductStmt.setDouble(1, amount);
                deductStmt.setString(2, userAccountId);
                int rowsAffected = deductStmt.executeUpdate();
                if (rowsAffected != 1) {
                    conn.rollback();
                    return false;
                }
            }

            String addSql = "UPDATE user_accounts SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement addStmt = conn.prepareStatement(addSql)) {
                addStmt.setDouble(1, amount);
                addStmt.setString(2, recipientId);
                int rowsAffected = addStmt.executeUpdate();
                if (rowsAffected != 1) {
                    conn.rollback();
                    return false;
                }
            }

            String transactionId = dbConnection.generateTransactionId();
            String recordSql = "INSERT INTO transactions (transaction_id, from_user_id, to_user_id, transaction_date, amount, transaction_type, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";
            try (PreparedStatement recordStmt = conn.prepareStatement(recordSql)) {
                recordStmt.setString(1, transactionId);
                recordStmt.setString(2, userAccountId);
                recordStmt.setString(3, recipientId);
                recordStmt.setDouble(4, amount);
                recordStmt.setString(5, "Transfer");
                recordStmt.setDouble(6, currentBalance - amount);

                recordStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
