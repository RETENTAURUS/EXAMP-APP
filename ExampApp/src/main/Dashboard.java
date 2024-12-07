package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Dashboard extends JPanel {
    public Dashboard(CardLayout cardLayout, Container parentContainer) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Start Exam Button
        JButton startExamButton = createButton("Start Exam", new Color(34, 139, 34));
        startExamButton.addActionListener(e -> cardLayout.show(parentContainer, "ExamSelection"));
        gbc.gridy = 0;
        buttonPanel.add(startExamButton, gbc);

        // View Results Button
        JButton viewResultsButton = createButton("View Results", new Color(70, 130, 180));
        viewResultsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Results Displayed!"));
        gbc.gridy = 1;
        buttonPanel.add(viewResultsButton, gbc);

        // Exit Button
        JButton exitButton = createButton("Exit", Color.RED);
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 2;
        buttonPanel.add(exitButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExamApp app = new ExamApp();
            app.setVisible(true);
        });
    }

}
