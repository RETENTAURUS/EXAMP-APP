package models;

import interfaces.Score;

public class Essay extends Question implements Score {
    private String correctAnswer;

    public Essay(String questionText, double score, String correctAnswer) {
        super(questionText, score);
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean validateAnswer(String userAnswer) {
        return userAnswer.toLowerCase().contains(correctAnswer.toLowerCase());
    }

    @Override
    public double calculateScore() {
        return validateAnswer(correctAnswer) ? getScore() : getScore() / 2;
    }

    @Override
    public void displayQuestion() {
        super.displayQuestion();
        System.out.println("Essay Question: Provide your answer below.");
    }
}
