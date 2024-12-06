package models;

public abstract class Question {
    private String questionText;
    private double score;

    public Question(String questionText, double score) {
        this.questionText = questionText;
        this.score = score;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public abstract boolean validateAnswer(String userAnswer);

    public void displayQuestion() {
        System.out.println("Question: " + questionText);
    }
}
