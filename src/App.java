import java.awt.*;
import java.util.regex.Pattern;
import javax.swing.*;

public class App extends JFrame {
    DatabaseConnection dbConnection;

    public App() {
        setTitle("Online Banking System");
        setSize(825, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        dbConnection = new DatabaseConnection();
        initComponents();
    }

    private void initComponents() {
        JPanel leftPanel = createLeftPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JLabel signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(new Font("Helvetica", Font.BOLD, 17));
        signUpLabel.setForeground(Color.decode("#131842"));

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Helvetica", Font.BOLD, 17));
        loginLabel.setForeground(Color.decode("#131842"));

        tabbedPane.addTab(null, new InitialSignUpPanel(tabbedPane));
        tabbedPane.setTabComponentAt(0, signUpLabel);

        tabbedPane.addTab(null, new LoginPanel());
        tabbedPane.setTabComponentAt(1, loginLabel);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(tabbedPane);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#2E236C"));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel bankNameLabel = new JLabel("NEXGEN BANK", SwingConstants.CENTER);
        bankNameLabel.setFont(new Font("Algerian", Font.BOLD, 50));
        bankNameLabel.setForeground(Color.decode("#EEEDEB"));
        bankNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankImageLabel = new JLabel(new ImageIcon("src/resources/bank-logo.png"));
        bankImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankInfo = new JLabel("Welcome to NexGen! Providing the best banking services.", SwingConstants.CENTER);
        bankInfo.setFont(new Font("Helvetica", Font.BOLD, 13));
        bankInfo.setForeground(Color.decode("#EEEDEB"));
        bankInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel terms = new JLabel("© 2024 NEXGEN BANK. All rights reserved.", SwingConstants.CENTER);
        terms.setFont(new Font("Helvetica", Font.BOLD, 13));
        terms.setForeground(Color.decode("#EEEDEB"));
        terms.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(bankNameLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(bankImageLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 150)));
        leftPanel.add(bankInfo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(terms);
        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

    class InitialSignUpPanel extends JPanel {

        public InitialSignUpPanel(JTabbedPane tabbedPane) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            setBackground(Color.decode("#FAE7F3"));

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("Helvetica", Font.BOLD, 15));

            JTextField emailField = new JTextField(20);

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(emailLabel, gbc);
            gbc.gridx = 1;
            add(emailField, gbc);

            JLabel passwordLabel = new JLabel("Create Password:");
            passwordLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
            JPasswordField passwordField = new JPasswordField(20);
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passwordLabel, gbc);
            gbc.gridx = 1;
            add(passwordField, gbc);

            JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
            confirmPasswordLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
            JPasswordField confirmPasswordField = new JPasswordField(20);
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(confirmPasswordLabel, gbc);
            gbc.gridx = 1;
            add(confirmPasswordField, gbc);

            JButton submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Helvetica", Font.BOLD, 15));
            submitButton.setPreferredSize(new Dimension(90, 30));
            submitButton.setBackground(Color.decode("#7071E8"));
            submitButton.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            add(submitButton, gbc);

            submitButton.addActionListener(e -> {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
            
                if (password.equals(confirmPassword) && isValidEmail(email) && isValidPassword(password)) {
                    JOptionPane.showMessageDialog(null, "Signed up successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showAdditionalInfoForm(email, password, tabbedPane);
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match or invalid email/password format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        private void showAdditionalInfoForm(String email, String password, JTabbedPane tabbedPane) {
            JFrame formFrame = new JFrame("User Data Form");
            formFrame.setSize(500, 400);
            formFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            formFrame.setLocationRelativeTo(null);
            Form additionalInfoForm = new Form(dbConnection, tabbedPane, formFrame);
            additionalInfoForm.setEmail(email);
            additionalInfoForm.setPassword(password);
            formFrame.add(additionalInfoForm);
            formFrame.setVisible(true);
        }

        private boolean isValidEmail(String email) {
            String emailRegex = "^[a-zA-Z][a-zA-Z0-9]*@gmail\\.com$";
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches();
        }

        private boolean isValidPassword(String password) {
            String passwordRegex = "^[a-zA-Z0-9@_-]+$";
            Pattern pattern = Pattern.compile(passwordRegex);
            return pattern.matcher(password).matches();
        }
    }

    class LoginPanel extends JPanel {
        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            setBackground(Color.decode("#D1E9F6"));

            JLabel accountIdLabel = new JLabel("Account ID:");
            accountIdLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
            JTextField accountIdField = new JTextField(20);

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(accountIdLabel, gbc);
            gbc.gridx = 1;
            add(accountIdField, gbc);

            JLabel pinLabel = new JLabel("PIN:");
            pinLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
            JPasswordField pinField = new JPasswordField(20);
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(pinLabel, gbc);
            gbc.gridx = 1;
            add(pinField, gbc);

            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Helvetica", Font.BOLD, 15));
            loginButton.setPreferredSize(new Dimension(90, 30));
            loginButton.setBackground(Color.decode("#756AB6"));
            loginButton.setForeground(Color.WHITE); 
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            add(loginButton, gbc);

            loginButton.addActionListener(e -> {
                String accountId = accountIdField.getText();
                String pin = new String(pinField.getPassword());
                boolean loginSuccess = dbConnection.verifyUser(accountId, pin);
            
                if (loginSuccess) {
                    JOptionPane.showMessageDialog(null, "Logged in successfully!");
                    SwingUtilities.invokeLater(() -> {
                        String fullName = dbConnection.getUserFullName(accountId);
                        String accountType = dbConnection.getUserAccountType(accountId);
                        UserDashboard dashboard = new UserDashboard(fullName, accountId, accountType, dbConnection);
                        dashboard.setVisible(true);
                        SwingUtilities.getWindowAncestor(LoginPanel.this).dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid account ID or PIN.");
                }
            });  
        }
    }
}


