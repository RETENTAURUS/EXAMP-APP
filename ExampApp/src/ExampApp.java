import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExampApp {

    private static int currentQuestionIndex = 0;
    private static final String[][] questions = {
        {"What is the capital of France?", "Paris", "London", "Berlin", "Madrid"},
        {"What is 2 + 2?", "3", "4", "5", "6"},
        {"Who wrote 'Hamlet'?", "Charles Dickens", "William Shakespeare", "Mark Twain", "Ernest Hemingway"},
        {"What is the largest planet in our solar system?", "Earth", "Mars", "Jupiter", "Saturn"},
        {"What is the chemical symbol for water?", "H2O", "O2", "H2", "CO2"}
    };

    private static final String[] correctAnswers = {"Paris", "4", "William Shakespeare", "Jupiter", "H2O"};
    private static JFrame examFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exam App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setLayout(new BorderLayout());

            // Header Panel
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(70, 130, 180));
            headerPanel.setPreferredSize(new Dimension(600, 100));
            JLabel titleLabel = new JLabel("Welcome to Exam App");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);
            frame.add(headerPanel, BorderLayout.NORTH);

            // Main Panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.CENTER;

            // Start Exam Button
            JButton startExamButton = new JButton("Start Exam");
            startExamButton.setFont(new Font("Arial", Font.BOLD, 18));
            startExamButton.setBackground(new Color(34, 139, 34));
            startExamButton.setForeground(Color.WHITE);
            startExamButton.setPreferredSize(new Dimension(200, 50));
            startExamButton.addActionListener(e -> {
                frame.setVisible(false);
                showExamPage();
            });
            gbc.gridx = 0;
            gbc.gridy = 0;
            mainPanel.add(startExamButton, gbc);

            // View Results Button
            JButton viewResultsButton = new JButton("View Results");
            viewResultsButton.setFont(new Font("Arial", Font.BOLD, 18));
            viewResultsButton.setBackground(new Color(70, 130, 180));
            viewResultsButton.setForeground(Color.WHITE);
            viewResultsButton.setPreferredSize(new Dimension(200, 50));
            viewResultsButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "Results Displayed!");
            });
            gbc.gridy = 1;
            mainPanel.add(viewResultsButton, gbc);

            // Exit Button
            JButton exitButton = new JButton("Exit");
            exitButton.setFont(new Font("Arial", Font.BOLD, 18));
            exitButton.setBackground(Color.RED);
            exitButton.setForeground(Color.WHITE);
            exitButton.setPreferredSize(new Dimension(200, 50));
            exitButton.addActionListener(e -> System.exit(0));
            gbc.gridy = 2;
            mainPanel.add(exitButton, gbc);

            frame.add(mainPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static void showExamPage() {
        examFrame = new JFrame("Exam Page");
        examFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        examFrame.setSize(600, 400);
        examFrame.setLocationRelativeTo(null);
        examFrame.setLayout(new BorderLayout());

        // Header for Exam Page
        JPanel examHeader = new JPanel();
        examHeader.setBackground(new Color(70, 130, 180));
        JLabel examTitle = new JLabel("Exam Started!");
        examTitle.setFont(new Font("Arial", Font.BOLD, 24));
        examTitle.setForeground(Color.WHITE);
        examHeader.add(examTitle);
        examFrame.add(examHeader, BorderLayout.NORTH);

        // Question Panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBackground(Color.WHITE);

        // Question Label
        JLabel questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1));
        ButtonGroup optionsGroup = new ButtonGroup();
        JRadioButton[] optionButtons = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        examFrame.add(questionPanel, BorderLayout.CENTER);

        // Navigation Panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(1, 10));

        for (int i = 0; i < 10; i++) {
            JButton nextButton = new JButton(String.valueOf(i + 1));
            final int questionIndex = i;
            nextButton.addActionListener(e -> {
                if (questionIndex < questions.length) {
                    currentQuestionIndex = questionIndex;
                    updateQuestion(questionLabel, optionButtons);
                }
            });
            navigationPanel.add(nextButton);
        }

        examFrame.add(navigationPanel, BorderLayout.SOUTH);

        // Submit Button
        JButton submitButton = new JButton("Submit Exam");
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitButton.setBackground(new Color(34, 139, 34));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String selectedAnswer = "";
            for (JRadioButton button : optionButtons) {
                if (button.isSelected()) {
                    selectedAnswer = button.getText();
                    break;
                }
            }
            if (selectedAnswer.isEmpty()) {
                JOptionPane.showMessageDialog(examFrame, "Please select an answer!");
            } else {
                JOptionPane.showMessageDialog(examFrame, "Exam Submitted!\nYour answer: " + selectedAnswer);
                examFrame.dispose();
            }
        });
        examFrame.add(submitButton, BorderLayout.EAST);

        updateQuestion(questionLabel, optionButtons);
        examFrame.setVisible(true);
    }

    private static void updateQuestion(JLabel questionLabel, JRadioButton[] optionButtons) {
        if (currentQuestionIndex < questions.length) {
            questionLabel.setText(questions[currentQuestionIndex][0]);
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(questions[currentQuestionIndex][i + 1]);
                optionButtons[i].setSelected(false);
            }
        }
    }
}