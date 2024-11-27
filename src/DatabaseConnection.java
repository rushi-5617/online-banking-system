import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_mysql_dbName";
    private static final String USER = "your_mysql_username";
    private static final String PASS = "your_mysql_password";
    private Connection conn;

    public DatabaseConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean createUserAccount(String email, String password, String fullName, Date dob, String address, String panNo, String accountType, String gender, String accountId, String pin) {
        String sql = "INSERT INTO user_accounts (email, password, full_name, dob, address, pan_number, account_type, gender, account_id, pin, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setDate(4, dob);
            pstmt.setString(5, address);
            pstmt.setString(6, panNo);
            pstmt.setString(7, accountType);
            pstmt.setString(8, gender);
            pstmt.setString(9, accountId);
            pstmt.setString(10, pin);
            pstmt.setDouble(11, 0.00);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account created successfully.");
                return true;
            } else {
                System.out.println("Failed to create account.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserFullName(String accountId) {
        String sql = "SELECT full_name FROM user_accounts WHERE account_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getUserAccountType(String accountId) {
        String sql = "SELECT account_type FROM user_accounts WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("account_type");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateBalance(String accountId, double newBalance) {
        String sql = "UPDATE user_accounts SET balance = ? WHERE account_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Balance updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update balance.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recordTransaction(String fromAccountId, String toAccountId, Date transactionDate, double amount, String transactionType) {
        String sql = "INSERT INTO transactions (from_user_id, to_user_id, transaction_date, amount, transaction_type) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromAccountId);
            pstmt.setString(2, toAccountId);
            pstmt.setDate(3, transactionDate);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, transactionType);
             
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction recorded successfully.");
                return true;
            } else {
                System.out.println("Failed to record transaction.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyUser(String accountId, String pin) {
        String sql = "SELECT * FROM user_accounts WHERE account_id = ? AND pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            pstmt.setString(2, pin);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getBalance(String accountId) {
        String sql = "SELECT balance FROM user_accounts WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                System.out.println("Account not found.");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

        public String generateTransactionId() {
            return "NXG" + (int)(Math.random() * 90 + 10) + (char)(Math.random() * 26 + 'A') + (char)(Math.random() * 26 + 'A') + (int)(Math.random() * 10);
        }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

