package models;

public abstract class Pertanyaan {
    private String teksPertanyaan;
    private double skor;

    public Pertanyaan(String teksPertanyaan, double skor) {
        this.teksPertanyaan = teksPertanyaan;
        this.skor = skor;
    }

    public String getTeksPertanyaan() {
        return teksPertanyaan;
    }

    public void setTeksPertanyaan(String teksPertanyaan) {
        this.teksPertanyaan = teksPertanyaan;
    }

    public double getSkor() {
        return skor;
    }

    public void setSkor(double skor) {
        this.skor = skor;
    }

    public abstract boolean validasiJawaban(String jawabanUser);

    public abstract void tampilkanPertanyaan();
}

// package models;

// public abstract class Pertanyaan {
// private String Question;
// private double score;

// public Pertanyaan(String Question, double score) {
// this.Question = Question;
// this.score = score;
// }

// public String getQuestion() {
// return Question;
// }

// public void setQuestion(String Question) {
// this.Question = Question;
// }

// public double getScore() {
// return score;
// }

// public void setScore(double score) {
// this.score = score;
// }

// public abstract boolean validateAnswer(String userAnswer);

// public void displayQuestion() {
// System.out.println("Question: " + Question);
// }
// }
