import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class ChangePinFrame extends JFrame {
    JPasswordField oldPinField;
    JPasswordField newPinField;
    JPasswordField confirmNewPinField;
    JButton changePinButton;
    DatabaseConnection dbConnection;
    String accountId;

    public ChangePinFrame(DatabaseConnection dbConnection, String accountId) {
        this.dbConnection = dbConnection;
        this.accountId = accountId;

        setTitle("Change PIN");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#17153B"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel oldPinLabel = new JLabel("Old PIN:");
        oldPinLabel.setForeground(Color.WHITE);
        oldPinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        oldPinField = new JPasswordField(10);

        JLabel newPinLabel = new JLabel("New PIN:");
        newPinLabel.setForeground(Color.WHITE);
        newPinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        newPinField = new JPasswordField(10);

        JLabel confirmNewPinLabel = new JLabel("Confirm New PIN:");
        confirmNewPinLabel.setForeground(Color.WHITE);
        confirmNewPinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        confirmNewPinField = new JPasswordField(10);

        changePinButton = new JButton("Change PIN");
        changePinButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        changePinButton.setBackground(Color.decode("#FF0000"));
        changePinButton.setForeground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(oldPinLabel, gbc);

        gbc.gridx = 1;
        panel.add(oldPinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(newPinLabel, gbc);

        gbc.gridx = 1;
        panel.add(newPinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(confirmNewPinLabel, gbc);

        gbc.gridx = 1;
        panel.add(confirmNewPinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(changePinButton, gbc);

        add(panel);

        changePinButton.addActionListener(e -> processPinChange());

        setVisible(true);
    }

    private void processPinChange() {
        String oldPin = new String(oldPinField.getPassword());
        String newPin = new String(newPinField.getPassword());
        String confirmNewPin = new String(confirmNewPinField.getPassword());

        if (newPin.equals(confirmNewPin)) {
            if (dbConnection.verifyUser(accountId, oldPin)) {
                String updatePinSql = "UPDATE user_accounts SET pin = ? WHERE account_id = ?";
                try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(updatePinSql)) {
                    pstmt.setString(1, newPin);
                    pstmt.setString(2, accountId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "PIN changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to change PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Old PIN is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "New PINs do not match.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
