package main;

import admin.AdminAppGUI;
import java.awt.*;
import javax.swing.*;

public class MainLauncher extends JFrame {
    private LoginHandler loginHandler;
    private String roles;

    public MainLauncher() {
        loginHandler = new LoginHandler();

        setTitle("Main Launcher - ExamApp");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("ExamApp Launcher", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton adminGuiButton = createButton("Admin");
        adminGuiButton.addActionListener(e -> {
            if (login("admin")) {
                SwingUtilities.invokeLater(AdminAppGUI::new);
            }
        });

        JButton examAppButton = createButton("Exam App");
        examAppButton.addActionListener(e -> {
            if (login("participant")) {
                SwingUtilities.invokeLater(() -> {
                    ExamApp examApp = new ExamApp();
                    examApp.setVisible(true);
                });
            }
        });

        JButton registerButton = createButton("Register");
        registerButton.addActionListener(e -> register());

        JButton exitButton = createButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(adminGuiButton);
        buttonPanel.add(examAppButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private boolean login(String requiredRole) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField usernames = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernames);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernames.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both username and password are required!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            roles = loginHandler.login(username, password);
            if (roles != null && roles.equals(requiredRole)) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + requiredRole, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else if (roles != null) {
                JOptionPane.showMessageDialog(this, "You do not have access to this menu!", "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    private void register() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernames = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] { "participant", "admin" });

        panel.add(new JLabel("Username:"));
        panel.add(usernames);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernames.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = loginHandler.register(username, password, role);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainLauncher::new);
    }
}
