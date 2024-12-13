package main;

import admin.AdminAppGUI;
import java.awt.*;
import javax.swing.*;

public class LoginGUI {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(240, 240, 240));
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("EXAMP-APP//ExampApp//icon//logo2.png"));
        logoPanel.add(logoLabel);
        panel.add(logoPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        LoginHandler loginHandler = new LoginHandler();

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Both username and password are required!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String role = loginHandler.login(username, password);
            if (role != null) {

                if (role.equals("admin")) {
                    JOptionPane.showMessageDialog(frame, "Welcome Admin!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    SwingUtilities.invokeLater(AdminAppGUI::new);
                } else if (role.equals("participant")) {
                    JOptionPane.showMessageDialog(frame, "Welcome Participant!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    SwingUtilities.invokeLater(() -> {
                        ExamApp examApp = new ExamApp();
                        examApp.setVisible(true);
                    });
                }
                frame.dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(frame, "Enter username:");
            String password = JOptionPane.showInputDialog(frame, "Enter password:");
            String[] roles = { "admin", "participant" };
            String role = (String) JOptionPane.showInputDialog(frame, "Select role:", "Role Selection",
                    JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);

            if (username != null && password != null && role != null) {
                loginHandler.register(username, password, role);
            }
        });

        JLabel footerLabel = new JLabel("\u00A9 2024 ExampApp", SwingConstants.CENTER);
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(footerLabel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }
}