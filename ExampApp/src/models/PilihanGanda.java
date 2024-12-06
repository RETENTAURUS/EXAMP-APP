package models;

public class PilihanGanda extends Pertanyaan {
    private String[] opsi;
    private String jawabanBenar;

    public PilihanGanda(String teksPertanyaan, double skor, String[] opsi, String jawabanBenar) {
        super(teksPertanyaan, skor);
        this.opsi = opsi;
        this.jawabanBenar = jawabanBenar;
    }

    public String[] getOpsi() {
        return opsi;
    }

    public String getJawabanBenar() {
        return jawabanBenar;
    }

    @Override
    public boolean validasiJawaban(String jawabanUser) {
        return jawabanUser.equalsIgnoreCase(jawabanBenar);
    }

    @Override
    public void tampilkanPertanyaan() {
        System.out.println("Pertanyaan Pilihan Ganda: " + getTeksPertanyaan());
        for (int i = 0; i < opsi.length; i++) {
            System.out.println((char) ('A' + i) + ". " + opsi[i]);
        }
    }
}

// package models;

// import interfaces.Score;

// public class PilihanGanda extends Question implements Score {
// private String[] options;
// private String correctAnswer;

// public PilihanGanda(String Question, double score, String[] options, String
// correctAnswer) {
// super(Question, score);
// this.options = options;
// this.correctAnswer = correctAnswer;
// }

// public String[] getOptions() {
// return options;
// }

// public void setOptions(String[] options) {
// this.options = options;
// }

// public String getCorrectAnswer() {
// return correctAnswer;
// }

// public void setCorrectAnswer(String correctAnswer) {
// this.correctAnswer = correctAnswer;
// }

// @Override
// public boolean validateAnswer(String userAnswer) {
// return userAnswer.equalsIgnoreCase(correctAnswer);
// }

// @Override
// public double calculateScore() {
// return validateAnswer(correctAnswer) ? getScore() : 0;
// }

// @Override
// public void displayQuestion() {
// super.displayQuestion();
// for (int i = 0; i < options.length; i++) {
// System.out.println((char) ('A' + i) + ". " + options[i]);
// }
// }
// }
