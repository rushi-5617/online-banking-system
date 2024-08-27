import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ViewBalanceFrame extends JFrame {
    private JPasswordField pinField;
    JButton viewBalanceButton;

    public ViewBalanceFrame(DatabaseConnection dbConnection, String accountId) {
        setTitle("View Balance");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#17153B"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Helvetica", Font.BOLD, 16));

        pinField = new JPasswordField(2);
        pinField.setFont(new Font("Helvetica", Font.PLAIN, 16));

        viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        viewBalanceButton.setPreferredSize(new Dimension(135, 40));
        viewBalanceButton.setBackground(Color.decode("#FF0000"));
        viewBalanceButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(pinLabel, gbc);

        gbc.gridx = 1;
        panel.add(pinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(viewBalanceButton, gbc);

        add(panel);

        viewBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pin = new String(pinField.getPassword());
                if (dbConnection.verifyUser(accountId, pin)) {
                    double balance = dbConnection.getBalance(accountId);
                    if (balance != -1) {
                        JOptionPane.showMessageDialog(ViewBalanceFrame.this, "Your balance is: ₹" + balance, "Balance", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ViewBalanceFrame.this, "Failed to retrieve balance.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ViewBalanceFrame.this, "Invalid PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        setVisible(true);
    }
}

