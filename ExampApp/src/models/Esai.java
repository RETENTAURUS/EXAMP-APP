package models;

public class Esai extends Pertanyaan {
    private String jawabanBenar;

    public Esai(String teksPertanyaan, double skor, String jawabanBenar) {
        super(teksPertanyaan, skor);
        this.jawabanBenar = jawabanBenar;
    }

    public String getJawabanBenar() {
        return jawabanBenar;
    }

    public void setJawabanBenar(String jawabanBenar) {
        this.jawabanBenar = jawabanBenar;
    }

    @Override
    public boolean validasiJawaban(String jawabanUser) {
        return jawabanUser.toLowerCase().contains(jawabanBenar.toLowerCase());
    }

    @Override
    public void tampilkanPertanyaan() {
        System.out.println("Pertanyaan Esai: " + getTeksPertanyaan());
    }
}

// package models;

// import interfaces.Score;

// public class Esai extends Question implements Score {
// private String correctAnswer;

// public Esai(String Question, double score, String correctAnswer) {
// super(Question, score);
// this.correctAnswer = correctAnswer;
// }

// public String getCorrectAnswer() {
// return correctAnswer;
// }

// public void setCorrectAnswer(String correctAnswer) {
// this.correctAnswer = correctAnswer;
// }

// @Override
// public boolean validateAnswer(String userAnswer) {
// return userAnswer.toLowerCase().contains(correctAnswer.toLowerCase());
// }

// @Override
// public double calculateScore() {
// return validateAnswer(correctAnswer) ? getScore() : getScore() / 2;
// }

// @Override
// public void displayQuestion() {
// super.displayQuestion();
// System.out.println("Essay Question: Provide your answer below.");
// }
// }
