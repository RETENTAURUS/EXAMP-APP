package main;

import java.sql.*;
import java.util.*;
import models.*;
import utils.DatabaseConnection;

public class ExamApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> availableExams = new ArrayList<>();
            Map<Integer, String> examPasswords = new HashMap<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, password FROM exams");

            while (rs.next()) {
                availableExams.add(rs.getString("name"));
                examPasswords.put(rs.getInt("id"), rs.getString("password"));
            }

            System.out.println("=== Welcome to the Exam App ===");
            System.out.println("Available Exams:");
            for (int i = 0; i < availableExams.size(); i++) {
                System.out.println((i + 1) + ". " + availableExams.get(i));
            }

            System.out.print("\nSelect an exam by entering the number: ");
            int selectedExamIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (selectedExamIndex < 0 || selectedExamIndex >= availableExams.size()) {
                System.out.println("Invalid selection. Exiting...");
                return;
            }

            String selectedExam = availableExams.get(selectedExamIndex);
            int selectedExamId = selectedExamIndex + 1;
            String examPassword = examPasswords.get(selectedExamId);
            System.out.println("\nYou selected: " + selectedExam);
            System.out.print("\nEnter your full name: ");
            String fullName = scanner.nextLine();

            boolean authenticated = false;
            int attempts = 0;

            while (!authenticated && attempts < 3) {
                System.out.print("Enter exam password: ");
                String password = scanner.nextLine();

                if (password.equals(examPassword)) {
                    authenticated = true;
                } else {
                    attempts++;
                    System.out.println("Incorrect password. Please try again.");
                }
            }

            if (!authenticated) {
                System.out.println("Too many failed attempts. Exiting...");
                return;
            }

            List<Question> questions = getQuestionsFromDatabase(conn, selectedExamId);
            System.out.println("=== Exam Questions ===");
            double totalScore = 0;
            double maxScore = 0;
            List<String> userAnswers = new ArrayList<>();
            List<String> correctAnswers = new ArrayList<>();

            for (Question question : questions) {
                question.displayQuestion();
                System.out.print("Your Answer: ");
                String userAnswer = scanner.nextLine();
                userAnswers.add(userAnswer);
                maxScore += question.getScore();

                if (question instanceof MultipleChoice) {
                    correctAnswers.add(((MultipleChoice) question).getCorrectAnswer());
                } else if (question instanceof Essay) {
                    correctAnswers.add(((Essay) question).getCorrectAnswer());
                }

                if (question.validateAnswer(userAnswer)) {
                    totalScore += question.getScore();
                }
            }

            double finalScore = (totalScore / maxScore) * 100;

            String answersJson = formatAnswersForDatabase(userAnswers, correctAnswers);
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (name, exam_id, answers, final_score) VALUES (?, ?, ?, ?)");
            userStmt.setString(1, fullName);
            userStmt.setInt(2, selectedExamId);
            userStmt.setString(3, answersJson);
            userStmt.setDouble(4, finalScore);
            userStmt.executeUpdate();

            System.out.println("\n=== Exam Review ===");
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                System.out.println("Question " + (i + 1) + ": " + question.getQuestionText());
                System.out.println("Your Answer: " + userAnswers.get(i));
                System.out.println("Correct Answer: " + correctAnswers.get(i));
                System.out.println();
            }

            System.out.println("=== Exam Completed ===");
            System.out.println("Your Total Score: " + finalScore + " / 100");
            System.out.println("Thank you for taking the exam!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Question> getQuestionsFromDatabase(Connection conn, int examId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT question_text, score, options, correct_answer, type FROM questions WHERE exam_id = ?");
        stmt.setInt(1, examId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String questionText = rs.getString("question_text");
            double score = rs.getDouble("score");
            String options = rs.getString("options");
            String correctAnswer = rs.getString("correct_answer");
            String type = rs.getString("type");

            if (type.equals("MC")) {
                String[] optionArray = options.split(",");
                questions.add(new MultipleChoice(questionText, score, optionArray, correctAnswer));
            } else if (type.equals("Essay")) {
                questions.add(new Essay(questionText, score, correctAnswer));
            }
        }
        return questions;
    }

    private static String formatAnswersForDatabase(List<String> userAnswers, List<String> correctAnswers) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < userAnswers.size(); i++) {
            json.append("{");
            json.append("\"userAnswer\": \"").append(userAnswers.get(i)).append("\",");
            json.append("\"correctAnswer\": \"").append(correctAnswers.get(i)).append("\"");
            json.append("}");
            if (i < userAnswers.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}
