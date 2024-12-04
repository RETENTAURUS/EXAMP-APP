import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class AdminGUI {
    private JFrame frame;
    private JTextField idField;
    private JTextField questionField;
    private JTextField optionAField;
    private JTextField optionBField;
    private JTextField optionCField;
    private JTextField optionDField;
    private JTextField correctAnswerField;
    private JTextField examIdField;
    private JComboBox<String> questionTypeComboBox;

    public AdminGUI() {
        createMainGUI();
    }

    private void createMainGUI() {
        frame = new JFrame("Admin Panel");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel("Admin Panel", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(70, 130, 180));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton updateButton = new JButton("Update Question");
        updateButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        updateButton.setBackground(new Color(255, 165, 0));
        updateButton.setForeground(Color.WHITE);
        buttonPanel.add(updateButton);

        JButton insertButton = new JButton("Insert Question");
        insertButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        insertButton.setBackground(new Color(60, 179, 113));
        insertButton.setForeground(Color.WHITE);
        buttonPanel.add(insertButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Add Action Listeners
        updateButton.addActionListener(e -> openUpdateFrame());
        insertButton.addActionListener(e -> openInsertFrame());

        frame.setVisible(true);
    }

    private void openUpdateFrame() {
        JFrame updateFrame = new JFrame("Update Question");
        updateFrame.setSize(500, 400);
        updateFrame.setLayout(new BorderLayout());

        // Header with Back Button
        JLabel headerLabel = new JLabel("Update Question", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(255, 165, 0));
        headerLabel.setForeground(Color.WHITE);

        JButton backButton = createBackButton(updateFrame);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        updateFrame.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("New Question:"));
        questionField = new JTextField();
        formPanel.add(questionField);

        formPanel.add(new JLabel("Question Type:"));
        questionTypeComboBox = new JComboBox<>(new String[]{"multiple_choice", "true_false", "short_answer"});
        formPanel.add(questionTypeComboBox);

        formPanel.add(new JLabel("Option A:"));
        optionAField = new JTextField();
        formPanel.add(optionAField);

        formPanel.add(new JLabel("Option B:"));
        optionBField = new JTextField();
        formPanel.add(optionBField);

        formPanel.add(new JLabel("Option C:"));
        optionCField = new JTextField();
        formPanel.add(optionCField);

        formPanel.add(new JLabel("Option D:"));
        optionDField = new JTextField();
        formPanel.add(optionDField);

        formPanel.add(new JLabel("Correct Answer:"));
        correctAnswerField = new JTextField();
        formPanel.add(correctAnswerField);

        JButton submitButton = new JButton("Update");
        submitButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String newQuestionText = questionField.getText();
                String questionType = (String) questionTypeComboBox.getSelectedItem();
                String choiceA = optionAField.getText();
                String choiceB = optionBField.getText();
                String choiceC = optionCField.getText();
                String choiceD = optionDField.getText();
                String newCorrectAnswer = correctAnswerField.getText();

                if (newQuestionText.isEmpty() || newCorrectAnswer.isEmpty()) {
                    JOptionPane.showMessageDialog(updateFrame, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseManager dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/examp", "root", "alfin");
                dbManager.connect();
                dbManager.updateQuestionData(id, newQuestionText, questionType, choiceA, choiceB, choiceC, choiceD, newCorrectAnswer);
                dbManager.disconnect();

                JOptionPane.showMessageDialog(updateFrame, "Question updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(updateFrame, "ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(updateFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateFrame.add(formPanel, BorderLayout.CENTER);
        updateFrame.add(submitButton, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    private void openInsertFrame() {
        JFrame insertFrame = new JFrame("Insert Question");
        insertFrame.setSize(500, 500);
        insertFrame.setLayout(new BorderLayout());

        // Header with Back Button
        JLabel headerLabel = new JLabel("Insert Question", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(60, 179, 113));
        headerLabel.setForeground(Color.WHITE);

        JButton backButton = createBackButton(insertFrame);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        insertFrame.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Question:"));
        questionField = new JTextField();
        formPanel.add(questionField);

        formPanel.add(new JLabel("Question Type:"));
        questionTypeComboBox = new JComboBox<>(new String[]{"multiple_choice", "true_false", "short_answer"});
        formPanel.add(questionTypeComboBox);

        formPanel.add(new JLabel("Option A:"));
        optionAField = new JTextField();
        formPanel.add(optionAField);

        formPanel.add(new JLabel("Option B:"));
        optionBField = new JTextField();
        formPanel.add(optionBField);

        formPanel.add(new JLabel("Option C:"));
        optionCField = new JTextField();
        formPanel.add(optionCField);

        formPanel.add(new JLabel("Option D:"));
        optionDField = new JTextField();
        formPanel.add(optionDField);

        formPanel.add(new JLabel("Correct Answer (A/B/C/D):"));
        correctAnswerField = new JTextField();
        formPanel.add(correctAnswerField);

        formPanel.add(new JLabel("Exam ID:"));
        examIdField = new JTextField();
        formPanel.add(examIdField);

        JButton submitButton = new JButton("Insert");
        submitButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String questionText = questionField.getText();
                String questionType = (String) questionTypeComboBox.getSelectedItem();
                String optionA = optionAField.getText();
                String optionB = optionBField.getText();
                String optionC = optionCField.getText();
                String optionD = optionDField.getText();
                String correctAnswer = correctAnswerField.getText();
                int examId = Integer.parseInt(examIdField.getText());

                if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || correctAnswer.isEmpty()) {
                    JOptionPane.showMessageDialog(insertFrame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseManager dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/examp", "root", "alfin");
                dbManager.connect();
                dbManager.insertQuestionData(id, questionText, questionType, optionA, optionB, optionC, optionD, correctAnswer, examId);
                dbManager.disconnect();

                JOptionPane.showMessageDialog(insertFrame, "Question inserted successfully!");
                clearInsertFields(); // Clear input fields after insertion
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(insertFrame, "ID and Exam ID must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(insertFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        insertFrame.add(formPanel, BorderLayout.CENTER);
        insertFrame.add(submitButton, BorderLayout.SOUTH);
        insertFrame.setVisible(true);
    }

    private void clearInsertFields() {
        idField.setText("");
        questionField.setText("");
        optionAField.setText("");
        optionBField.setText("");
        optionCField.setText("");
        optionDField.setText("");
        correctAnswerField.setText("");
        examIdField.setText("");
    }

    private JButton createBackButton(JFrame currentFrame) {
        // Load and resize image
        ImageIcon icon = new ImageIcon("ExampApp/icon/back.png"); // Path to image
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImg);

        // Create button with icon
        JButton backButton = new JButton(resizedIcon);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> currentFrame.dispose());

        return backButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminGUI::new);
    }
}