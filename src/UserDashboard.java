import java.awt.*;
import javax.swing.*;

public class UserDashboard extends JFrame {

    /*private String fullName;
    private String accountId;
    private String accountType;
    private DatabaseConnection dbConnection;*/

    public UserDashboard(String fullName, String accountId, String accountType, DatabaseConnection dbConnection) {
        /*this.fullName = fullName;
        this.accountId = accountId;
        this.accountType = accountType;
        this.dbConnection = dbConnection;*/

        setTitle("User Dashboard");
        setSize(825, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#17153B"));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.decode("#17153B"));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 75, 20, 0));
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.decode("#D8D9DA"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome, " + fullName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Helvetica", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.decode("#F6F5F5"));

        JLabel accountInfoLabel = new JLabel("Account ID: " + accountId + " | Account Type: " + accountType, SwingConstants.CENTER);
        accountInfoLabel.setForeground(Color.decode("#F6F5F5"));
        accountInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 15));

        infoPanel.add(welcomeLabel);
        infoPanel.add(accountInfoLabel);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Helvetica", Font.BOLD, 17));
        logoutButton.setBackground(Color.decode("#4793AF"));
        logoutButton.setForeground(Color.decode("#151515"));
        logoutButton.setPreferredSize(new Dimension(100, 20));

        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        JButton sendMoneyButton = createButton("Send Money");
        sendMoneyButton.setBackground(Color.decode("#58287F"));
        sendMoneyButton.setForeground(Color.decode("#F6F5F5"));

        JButton depositMoneyButton = createButton("Deposit Money");
        depositMoneyButton.setBackground(Color.decode("#58287F"));
        depositMoneyButton.setForeground(Color.decode("#F6F5F5"));

        JButton withdrawMoneyButton = createButton("Withdraw Money");
        withdrawMoneyButton.setBackground(Color.decode("#58287F"));
        withdrawMoneyButton.setForeground(Color.decode("#F6F5F5"));

        JButton viewBalanceButton = createButton("View Balance");
        viewBalanceButton.setBackground(Color.decode("#58287F"));
        viewBalanceButton.setForeground(Color.decode("#F6F5F5"));

        JButton changePinButton = createButton("Change PIN");
        changePinButton.setBackground(Color.decode("#58287F"));
        changePinButton.setForeground(Color.decode("#F6F5F5"));

        JButton viewTransactionHistoryButton = createButton("Transaction Log");
        viewTransactionHistoryButton.setBackground(Color.decode("#58287F"));
        viewTransactionHistoryButton.setForeground(Color.decode("#F6F5F5"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);
        buttonPanel.add(sendMoneyButton, gbc);
        gbc.gridx = 1;
        buttonPanel.add(depositMoneyButton, gbc);
        gbc.gridx = 2;
        buttonPanel.add(withdrawMoneyButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(viewBalanceButton, gbc);
        gbc.gridx = 1;
        buttonPanel.add(changePinButton, gbc);
        gbc.gridx = 2;
        buttonPanel.add(viewTransactionHistoryButton, gbc);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        sendMoneyButton.addActionListener(e -> new SendMoneyFrame(dbConnection, accountId).setVisible(true));

        depositMoneyButton.addActionListener(e -> new DepositMoneyFrame(dbConnection, accountId).setVisible(true));

        withdrawMoneyButton.addActionListener(e -> new WithdrawMoneyFrame(dbConnection, accountId).setVisible(true));

        viewBalanceButton.addActionListener(e -> new ViewBalanceFrame(dbConnection, accountId).setVisible(true));

        changePinButton.addActionListener(e -> new ChangePinFrame(dbConnection, accountId).setVisible(true));

        viewTransactionHistoryButton.addActionListener(e -> new TransactionHistoryFrame(accountId).setVisible(true));

        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                UserDashboard.this,
        "Are you sure you want to log out?",
          "Log Out Confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (response == JOptionPane.OK_OPTION) {
                dispose();
                new App().setVisible(true);
            }
        });

        add(mainPanel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }
}


