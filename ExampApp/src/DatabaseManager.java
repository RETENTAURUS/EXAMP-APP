import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection connection;

    public DatabaseManager(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            LOGGER.info("Database connected successfully.");
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            LOGGER.info("Database disconnected successfully.");
        }
    }

    public void updateQuestionData(int id, String newQuestionText, String questionType, String choiceA, String choiceB, String choiceC, String choiceD, String newCorrectAnswer) throws SQLException {
        ensureConnected();
        String sql = "UPDATE questions SET question_text = ?, question_type = ?, choice_a = ?, choice_b = ?, choice_c = ?, choice_d = ?, correct_answer = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newQuestionText);
            statement.setString(2, questionType);
            statement.setString(3, choiceA);
            statement.setString(4, choiceB);
            statement.setString(5, choiceC);
            statement.setString(6, choiceD);
            statement.setString(7, newCorrectAnswer);
            statement.setInt(8, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info("Data updated successfully for ID: " + id);
            } else {
                LOGGER.warning("No data found with ID: " + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update question data: " + e.getMessage(), e);
            throw e; // Rethrow the exception for upper-layer handling
        }
    }

    public void insertQuestionData(int id, String questionText, String questionType, String choiceA, String choiceB, String choiceC, String choiceD, String correctAnswer, int examId) throws SQLException {
        ensureConnected();
        String sql = "INSERT INTO questions (id, question_text, question_type, choice_a, choice_b, choice_c, choice_d, correct_answer, exam_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, questionText);
            statement.setString(3, questionType);
            statement.setString(4, choiceA);
            statement.setString(5, choiceB);
            statement.setString(6, choiceC);
            statement.setString(7, choiceD);
            statement.setString(8, correctAnswer);
            statement.setInt(9, examId);
            statement.executeUpdate();
            LOGGER.info("Question inserted successfully: " + questionText);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert question: " + e.getMessage(), e);
            throw e; // Rethrow exception for upper-layer handling
        }
    }

    private void ensureConnected() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not established.");
        }
    }
}