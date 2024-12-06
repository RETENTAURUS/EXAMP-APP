package main;

import java.sql.*;
import java.util.*;
import models.*;
import utils.DatabaseConnection;

public class ExamApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection koneksi = DatabaseConnection.getConnection()) {
            List<String> daftarUjian = new ArrayList<>();
            Map<Integer, String> passwordUjian = new HashMap<>();
            Statement stmt = koneksi.createStatement();
            ResultSet hasil = stmt.executeQuery("SELECT id, name, password FROM exams");

            while (hasil.next()) {
                daftarUjian.add(hasil.getString("name"));
                passwordUjian.put(hasil.getInt("id"), hasil.getString("password"));
            }

            System.out.println("=== Selamat Datang di Aplikasi Ujian ===");
            System.out.println("Daftar Ujian:");
            for (int i = 0; i < daftarUjian.size(); i++) {
                System.out.println((i + 1) + ". " + daftarUjian.get(i));
            }

            System.out.print("\nPilih ujian dengan memasukkan nomor: ");
            int indeksUjianTerpilih = scanner.nextInt() - 1;
            scanner.nextLine();

            if (indeksUjianTerpilih < 0 || indeksUjianTerpilih >= daftarUjian.size()) {
                System.out.println("Pilihan tidak valid. Keluar...");
                return;
            }

            String ujianTerpilih = daftarUjian.get(indeksUjianTerpilih);
            int idUjianTerpilih = indeksUjianTerpilih + 1;
            String passwordUjianTerpilih = passwordUjian.get(idUjianTerpilih);
            System.out.println("\nAnda memilih: " + ujianTerpilih);
            System.out.print("\nMasukkan nama lengkap Anda: ");
            String namaLengkap = scanner.nextLine();

            boolean autentikasi = false;
            int percobaan = 0;

            while (!autentikasi && percobaan < 3) {
                System.out.print("Masukkan password ujian: ");
                String password = scanner.nextLine();

                if (password.equals(passwordUjianTerpilih)) {
                    autentikasi = true;
                } else {
                    percobaan++;
                    System.out.println("Password salah. Silakan coba lagi.");
                }
            }

            if (!autentikasi) {
                System.out.println("Terlalu banyak percobaan yang gagal. Keluar...");
                return;
            }

            List<Pertanyaan> daftarPertanyaan = ambilPertanyaanDariDatabase(koneksi, idUjianTerpilih);
            System.out.println("=== Pertanyaan Ujian ===");
            double skorTotal = 0;
            double skorMaksimum = 0;
            List<String> jawabanUser = new ArrayList<>();
            List<String> jawabanBenar = new ArrayList<>();

            for (Pertanyaan pertanyaan : daftarPertanyaan) {
                pertanyaan.tampilkanPertanyaan();
                System.out.print("Jawaban Anda: ");
                String jawabanUserSaatIni = scanner.nextLine();
                jawabanUser.add(jawabanUserSaatIni);
                skorMaksimum += pertanyaan.getSkor();

                if (pertanyaan instanceof PilihanGanda) {
                    jawabanBenar.add(((PilihanGanda) pertanyaan).getJawabanBenar());
                } else if (pertanyaan instanceof Esai) {
                    jawabanBenar.add(((Esai) pertanyaan).getJawabanBenar());
                }

                if (pertanyaan.validasiJawaban(jawabanUserSaatIni)) {
                    skorTotal += pertanyaan.getSkor();
                }
            }

            double skorAkhir = (skorTotal / skorMaksimum) * 100;

            String jawabanJson = formatJawaban(jawabanUser, jawabanBenar);
            PreparedStatement simpanPengguna = koneksi.prepareStatement(
                    "INSERT INTO users (name, exam_id, answers, final_score) VALUES (?, ?, ?, ?)");
            simpanPengguna.setString(1, namaLengkap);
            simpanPengguna.setInt(2, idUjianTerpilih);
            simpanPengguna.setString(3, jawabanJson);
            simpanPengguna.setDouble(4, skorAkhir);
            simpanPengguna.executeUpdate();

            System.out.println("\n=== Ulasan Ujian ===");
            for (int i = 0; i < daftarPertanyaan.size(); i++) {
                Pertanyaan pertanyaan = daftarPertanyaan.get(i);
                System.out.println("Pertanyaan " + (i + 1) + ": " + pertanyaan.getTeksPertanyaan());
                System.out.println("Jawaban Anda: " + jawabanUser.get(i));
                System.out.println("Jawaban Benar: " + jawabanBenar.get(i));
                System.out.println();
            }

            System.out.println("=== Ujian Selesai ===");
            System.out.println("Skor Akhir Anda: " + skorAkhir + " / 100");
            System.out.println("Terima kasih telah mengikuti ujian!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Pertanyaan> ambilPertanyaanDariDatabase(Connection koneksi, int idUjian) throws SQLException {
        List<Pertanyaan> daftarPertanyaan = new ArrayList<>();
        PreparedStatement stmt = koneksi.prepareStatement(
                "SELECT question_text, score, options, correct_answer, type FROM questions WHERE exam_id = ?");
        stmt.setInt(1, idUjian);
        ResultSet hasil = stmt.executeQuery();

        while (hasil.next()) {
            String teksPertanyaan = hasil.getString("question_text");
            double skor = hasil.getDouble("score");
            String opsi = hasil.getString("options");
            String jawabanBenar = hasil.getString("correct_answer");
            String tipe = hasil.getString("type");

            if (tipe.equals("MC")) {
                String[] arrayOpsi = opsi.split(",");
                daftarPertanyaan.add(new PilihanGanda(teksPertanyaan, skor, arrayOpsi, jawabanBenar));
            } else if (tipe.equals("Essay")) {
                daftarPertanyaan.add(new Esai(teksPertanyaan, skor, jawabanBenar));
            }
        }
        return daftarPertanyaan;
    }

    private static String formatJawaban(List<String> jawabanUser, List<String> jawabanBenar) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < jawabanUser.size(); i++) {
            json.append("{");
            json.append("\"jawabanUser\": \"").append(jawabanUser.get(i)).append("\",");
            json.append("\"jawabanBenar\": \"").append(jawabanBenar.get(i)).append("\"");
            json.append("}");
            if (i < jawabanUser.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}

// package main;

// import java.sql.*;
// import java.util.*;
// import models.*;
// import utils.DatabaseConnection;

// public class ExamApp {
// public static void main(String[] args) {
// Scanner scanner = new Scanner(System.in);

// try (Connection conn = DatabaseConnection.getConnection()) {
// List<String> availableExams = new ArrayList<>();
// Map<Integer, String> examPasswords = new HashMap<>();
// Statement stmt = conn.createStatement();
// ResultSet rs = stmt.executeQuery("SELECT id, name, password FROM exams");

// while (rs.next()) {
// availableExams.add(rs.getString("name"));
// examPasswords.put(rs.getInt("id"), rs.getString("password"));
// }

// System.out.println("=== Welcome to the Exam App ===");
// System.out.println("Available Exams:");
// for (int i = 0; i < availableExams.size(); i++) {
// System.out.println((i + 1) + ". " + availableExams.get(i));
// }

// System.out.print("\nSelect an exam by entering the number: ");
// int selectedExamIndex = scanner.nextInt() - 1;
// scanner.nextLine();

// if (selectedExamIndex < 0 || selectedExamIndex >= availableExams.size()) {
// System.out.println("Invalid selection. Exiting...");
// return;
// }

// String selectedExam = availableExams.get(selectedExamIndex);
// int selectedExamId = selectedExamIndex + 1;
// String examPassword = examPasswords.get(selectedExamId);
// System.out.println("\nYou selected: " + selectedExam);
// System.out.print("\nEnter your full name: ");
// String fullName = scanner.nextLine();

// boolean authenticated = false;
// int attempts = 0;

// while (!authenticated && attempts < 3) {
// System.out.print("Enter exam password: ");
// String password = scanner.nextLine();

// if (password.equals(examPassword)) {
// authenticated = true;
// } else {
// attempts++;
// System.out.println("Incorrect password. Please try again.");
// }
// }

// if (!authenticated) {
// System.out.println("Too many failed attempts. Exiting...");
// return;
// }

// List<Question> questions = getQuestionsFromDB(conn, selectedExamId);
// System.out.println("=== Exam Questions ===");
// double totalScore = 0;
// double maxScore = 0;
// List<String> userAnswers = new ArrayList<>();
// List<String> correctAnswers = new ArrayList<>();

// for (Question question : questions) {
// question.displayQuestion();
// System.out.print("Your Answer: ");
// String userAnswer = scanner.nextLine();
// userAnswers.add(userAnswer);
// maxScore += question.getScore();

// if (question instanceof MultipleChoice) {
// correctAnswers.add(((MultipleChoice) question).getCorrectAnswer());
// } else if (question instanceof Essay) {
// correctAnswers.add(((Essay) question).getCorrectAnswer());
// }

// if (question.validateAnswer(userAnswer)) {
// totalScore += question.getScore();
// }
// }

// double finalScore = (totalScore / maxScore) * 100;

// String answersJson = formatAnswers(userAnswers, correctAnswers);
// PreparedStatement userStmt = conn.prepareStatement(
// "INSERT INTO users (name, exam_id, answers, final_score) VALUES (?, ?, ?,
// ?)");
// userStmt.setString(1, fullName);
// userStmt.setInt(2, selectedExamId);
// userStmt.setString(3, answersJson);
// userStmt.setDouble(4, finalScore);
// userStmt.executeUpdate();

// System.out.println("\n=== Exam Review ===");
// for (int i = 0; i < questions.size(); i++) {
// Question question = questions.get(i);
// System.out.println("Question " + (i + 1) + ": " + question.getQuestion());
// System.out.println("Your Answer: " + userAnswers.get(i));
// System.out.println("Correct Answer: " + correctAnswers.get(i));
// System.out.println();
// }

// System.out.println("=== Exam Completed ===");
// System.out.println("Your Total Score: " + finalScore + " / 100");
// System.out.println("Thank you for taking the exam!");
// } catch (SQLException e) {
// e.printStackTrace();
// }
// }

// private static List<Question> getQuestionsFromDB(Connection conn, int examId)
// throws SQLException {
// List<Question> questions = new ArrayList<>();
// PreparedStatement stmt = conn.prepareStatement(
// "SELECT question_text, score, options, correct_answer, type FROM questions
// WHERE exam_id = ?");
// stmt.setInt(1, examId);
// ResultSet rs = stmt.executeQuery();

// while (rs.next()) {
// String Question = rs.getString("question_text");
// double score = rs.getDouble("score");
// String options = rs.getString("options");
// String correctAnswer = rs.getString("correct_answer");
// String type = rs.getString("type");

// if (type.equals("MC")) {
// String[] optionArray = options.split(",");
// questions.add(new MultipleChoice(Question, score, optionArray,
// correctAnswer));
// } else if (type.equals("Essay")) {
// questions.add(new Essay(Question, score, correctAnswer));
// }
// }
// return questions;
// }

// private static String formatAnswers(List<String> userAnswers, List<String>
// correctAnswers) {
// StringBuilder json = new StringBuilder();
// json.append("[");
// for (int i = 0; i < userAnswers.size(); i++) {
// json.append("{");
// json.append("\"userAnswer\": \"").append(userAnswers.get(i)).append("\",");
// json.append("\"correctAnswer\":
// \"").append(correctAnswers.get(i)).append("\"");
// json.append("}");
// if (i < userAnswers.size() - 1) {
// json.append(",");
// }
// }
// json.append("]");
// return json.toString();
// }
// }
