package models;

import interfaces.Score;

public class MultipleChoice extends Question implements Score {
    private String[] options;
    private String correctAnswer;

    public MultipleChoice(String questionText, double score, String[] options, String correctAnswer) {
        super(questionText, score);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean validateAnswer(String userAnswer) {
        return userAnswer.equalsIgnoreCase(correctAnswer);
    }

    @Override
    public double calculateScore() {
        return validateAnswer(correctAnswer) ? getScore() : 0;
    }

    @Override
    public void displayQuestion() {
        super.displayQuestion();
        for (int i = 0; i < options.length; i++) {
            System.out.println((char) ('A' + i) + ". " + options[i]);
        }
    }
}
