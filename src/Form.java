import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

public class Form extends JPanel {
    private JTextField fullNameField;
    private JTextField dobField;
    private JTextField addressField;
    private JTextField panNoField;
    private JComboBox<String> accountTypeComboBox;
    private JComboBox<String> genderComboBox;
    JCheckBox termsCheckBox;
    /*private DatabaseConnection dbConnection;
    private JTabbedPane tabbedPane;
    private JFrame parentFrame;*/
    private String email;
    private String password;

    public Form(DatabaseConnection dbConnection, JTabbedPane tabbedPane, JFrame parentFrame) {
        /*this.dbConnection = dbConnection;
        this.tabbedPane = tabbedPane;
        this.parentFrame = parentFrame;*/
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setBackground(Color.decode("#D1E9F6"));

        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        fullNameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(fullNameLabel, gbc);
        gbc.gridx = 1;
        add(fullNameField, gbc);

        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        dobField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(dobLabel, gbc);
        gbc.gridx = 1;
        add(dobField, gbc);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(genderLabel, gbc);
        gbc.gridx = 1;
        add(genderComboBox, gbc);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        addressField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(addressLabel, gbc);
        gbc.gridx = 1;
        add(addressField, gbc);

        JLabel panNoLabel = new JLabel("PAN Number:");
        panNoLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        panNoField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(panNoLabel, gbc);
        gbc.gridx = 1;
        add(panNoField, gbc);

        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setFont(new Font("Helvetica", Font.BOLD, 15));
        accountTypeComboBox = new JComboBox<>(new String[]{"Current", "Savings"});
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(accountTypeLabel, gbc);
        gbc.gridx = 1;
        add(accountTypeComboBox, gbc);

        JPanel termsPanel = new JPanel(new GridBagLayout());
        termsPanel.setBackground(Color.decode("#D1E9F6"));
        GridBagConstraints termsGbc = new GridBagConstraints();
        termsGbc.insets = new Insets(5, 5, 5, 5);
        termsGbc.anchor = GridBagConstraints.CENTER;
        termsCheckBox = new JCheckBox("I agree to the rules and regulations");
        termsCheckBox.setFont(new Font("Helvetica", Font.PLAIN, 15));
        termsCheckBox.setBackground(Color.decode("#D1E9F6"));
        termsPanel.add(termsCheckBox, termsGbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(termsPanel, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Helvetica", Font.BOLD, 15));
        submitButton.setPreferredSize(new Dimension(120, 30));
        submitButton.setBackground(Color.decode("#7071E8"));
        submitButton.setForeground(Color.WHITE);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            if (validateForm()) {
                String fullName = fullNameField.getText();
                Date dob = Date.valueOf(convertDateFormat(dobField.getText()));
                String address = addressField.getText();
                String panNo = panNoField.getText();
                String accountType = (String) accountTypeComboBox.getSelectedItem();
                String gender = (String) genderComboBox.getSelectedItem();
                String accountId = generateFormattedId();
                String pin = generateNumericPin(4);
        
                boolean success = dbConnection.createUserAccount(email, password, fullName, dob, address, panNo, accountType, gender, accountId, pin);
        
                if (success) {
                    JOptionPane.showMessageDialog(null, "Account created successfully!\n\nAccount ID: " + accountId + "\nPIN: " + pin);
                    tabbedPane.setSelectedIndex(1);
                    parentFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Error creating account. Please try again.");
                }
            }
        });
    }

    private boolean validateForm() {
        if (fullNameField.getText().isEmpty() || dobField.getText().isEmpty() || addressField.getText().isEmpty() || panNoField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields.");
            return false;
        }

        if (!termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(null, "You must agree to the rules and regulations.");
            return false;
        }

        try {
            Date.valueOf(convertDateFormat(dobField.getText()));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use DD-MM-YYYY.");
            return false;
        }

        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern pattern = Pattern.compile(panPattern);
        Matcher matcher = pattern.matcher(panNoField.getText());
        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(null, "Invalid PAN number format.");
            return false;
        }

        return true;
    }

    private String convertDateFormat(String dob) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return outputFormat.format(inputFormat.parse(dob));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateFormattedId() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        StringBuilder id = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) {
            int index = rnd.nextInt(letters.length());
            id.append(letters.charAt(index));
        }

        for (int i = 0; i < 4; i++) {
            int index = rnd.nextInt(numbers.length());
            id.append(numbers.charAt(index));
        }

        return id.toString();
    }

    private String generateNumericPin(int length) {
        String numbers = "0123456789";
        StringBuilder pin = new StringBuilder();
        Random rnd = new Random();
        while (pin.length() < length) {
            int index = rnd.nextInt(numbers.length());
            pin.append(numbers.charAt(index));
        }
        return pin.toString();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
