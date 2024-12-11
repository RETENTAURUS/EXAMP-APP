package main;

import javax.swing.*;
import java.awt.*;

public class LoginGUI {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Create the main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        // Create a panel for the logo
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(240, 240, 240));
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("ExampApp/icon/logo2.png")); // Replace with your logo path
        logoPanel.add(logoLabel);
        panel.add(logoPanel, BorderLayout.NORTH);

        // Create a panel for the login form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(240, 240, 240));

        // Create GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        // Add password label and text field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Add login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Add a footer
        JLabel footerLabel = new JLabel("\u00A9 2024 ExampApp", SwingConstants.CENTER);
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(footerLabel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        frame.add(panel);
        frame.setVisible(true);
    }
}