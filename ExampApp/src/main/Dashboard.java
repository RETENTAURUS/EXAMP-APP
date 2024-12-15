package main;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import utils.DatabaseConnection;

public class Dashboard extends JPanel {
    public Dashboard(CardLayout cardLayout, Container parentContainer) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton startExamButton = createButton("Start Exam", new Color(34, 139, 34));
        startExamButton.addActionListener(e -> cardLayout.show(parentContainer, "ExamSelection"));
        gbc.gridy = 0;
        buttonPanel.add(startExamButton, gbc);

        JButton viewResultsButton = createButton("View Results", new Color(70, 130, 180));
        viewResultsButton.addActionListener(e -> viewResults());
        gbc.gridy = 1;
        buttonPanel.add(viewResultsButton, gbc);

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

    private void viewResults() {
       JFrame frame = new JFrame("View Results");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Exam Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Name", "Exam ID", "Answers", "Final Score", "Exam Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

        // Center align text in table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Table header customization
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, exam_id, answers, final_score, exam_time FROM peserta")) {

            while (rs.next()) {
                String name = rs.getString("name");
                int examId = rs.getInt("exam_id");
                String answers = rs.getString("answers");
                double score = rs.getDouble("final_score");
                Timestamp examTime = rs.getTimestamp("exam_time");

                model.addRow(new Object[]{name, examId, answers, score, examTime});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching results: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        frame.add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(255, 0, 0));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        closeButton.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(closeButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new CardLayout());
            frame.add(new Dashboard((CardLayout) frame.getContentPane().getLayout(), frame.getContentPane()));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
