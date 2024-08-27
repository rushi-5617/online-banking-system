import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class TransactionHistoryFrame extends JFrame {

    String accountId;

    public TransactionHistoryFrame(String accountId) {
        this.accountId = accountId;

        setTitle("Transaction History");
        setSize(825, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Transaction ID");
        model.addColumn("From Account ID");
        model.addColumn("To Account ID");
        model.addColumn("Transaction Date");
        model.addColumn("Amount");
        model.addColumn("Transaction Type");
        model.addColumn("Balance");

        JTable table = new JTable(model);
        table.setFont(new Font("Helvetica", Font.BOLD, 12));
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Helvetica", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.decode("#FFFEDA"));

        add(scrollPane, BorderLayout.CENTER);
        loadTransactionHistory(model);
    }

    private void loadTransactionHistory(DefaultTableModel model) {
        String sql = "SELECT transaction_id, from_user_id, to_user_id, transaction_date, amount, transaction_type, balance " +
                     "FROM transactions " +
                     "WHERE from_user_id = ? OR to_user_id = ? " +
                     "ORDER BY transaction_date DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_db", "root", "r1u2s3h4i5@RK9");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountId);
            pstmt.setString(2, accountId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String transactionId = rs.getString("transaction_id");
                String fromAccountId = rs.getString("from_user_id");
                String toAccountId = rs.getString("to_user_id");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");
                double amount = rs.getDouble("amount");
                String transactionType = rs.getString("transaction_type");
                double balance = rs.getDouble("balance");

                model.addRow(new Object[]{
                    transactionId,
                    fromAccountId,
                    toAccountId,
                    transactionDate,
                    amount,
                    transactionType,
                    balance
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

