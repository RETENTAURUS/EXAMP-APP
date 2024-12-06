package admin;

import java.sql.*;
import java.util.Scanner;
import utils.DatabaseConnection;

public class AdminApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection koneksi = DatabaseConnection.getConnection()) {
            int pilihan;

            do {
                System.out.println("\n=== Panel Admin ===");
                System.out.println("1. Tambah Ujian");
                System.out.println("2. Lihat Daftar Ujian");
                System.out.println("3. Ubah Ujian");
                System.out.println("4. Hapus Ujian");
                System.out.println("5. Tambah Soal");
                System.out.println("6. Lihat Daftar Soal");
                System.out.println("7. Ubah Soal");
                System.out.println("8. Hapus Soal");
                System.out.println("0. Keluar");
                System.out.print("Masukkan pilihan Anda: ");
                pilihan = scanner.nextInt();
                scanner.nextLine();

                switch (pilihan) {
                    case 1:
                        tambahUjian(koneksi, scanner);
                        break;
                    case 2:
                        lihatDaftarUjian(koneksi);
                        break;
                    case 3:
                        ubahUjian(koneksi, scanner);
                        break;
                    case 4:
                        hapusUjian(koneksi, scanner);
                        break;
                    case 5:
                        tambahSoal(koneksi, scanner);
                        break;
                    case 6:
                        lihatDaftarSoal(koneksi, scanner);
                        break;
                    case 7:
                        ubahSoal(koneksi, scanner);
                        break;
                    case 8:
                        hapusSoal(koneksi, scanner);
                        break;
                    case 0:
                        System.out.println("Keluar dari Panel Admin...");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            } while (pilihan != 0);

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static void tambahUjian(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan judul ujian: ");
        String judul = scanner.nextLine();
        System.out.print("Masukkan password ujian: ");
        String password = scanner.nextLine();

        PreparedStatement stmt = koneksi.prepareStatement("INSERT INTO exams (name, password) VALUES (?, ?)");
        stmt.setString(1, judul);
        stmt.setString(2, password);
        stmt.executeUpdate();
        System.out.println("Ujian berhasil ditambahkan!");
    }

    private static void lihatDaftarUjian(Connection koneksi) throws SQLException {
        Statement stmt = koneksi.createStatement();
        ResultSet hasil = stmt.executeQuery("SELECT * FROM exams");

        System.out.println("\n=== Daftar Ujian ===");
        while (hasil.next()) {
            System.out.println("ID: " + hasil.getInt("id") + ", Judul: " + hasil.getString("name") + ", Password: "
                    + hasil.getString("password"));
        }
    }

    private static void ubahUjian(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID ujian yang ingin diubah: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan judul baru untuk ujian: ");
        String judul = scanner.nextLine();
        System.out.print("Masukkan password baru untuk ujian: ");
        String password = scanner.nextLine();

        PreparedStatement stmt = koneksi.prepareStatement("UPDATE exams SET name = ?, password = ? WHERE id = ?");
        stmt.setString(1, judul);
        stmt.setString(2, password);
        stmt.setInt(3, id);
        int barisTerubah = stmt.executeUpdate();
        if (barisTerubah > 0) {
            System.out.println("Ujian berhasil diubah!");
        } else {
            System.out.println("Ujian tidak ditemukan.");
        }
    }

    private static void hapusUjian(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID ujian yang ingin dihapus: ");
        int id = scanner.nextInt();

        PreparedStatement stmt = koneksi.prepareStatement("DELETE FROM exams WHERE id = ?");
        stmt.setInt(1, id);
        int barisTerhapus = stmt.executeUpdate();
        if (barisTerhapus > 0) {
            System.out.println("Ujian berhasil dihapus!");
        } else {
            System.out.println("Ujian tidak ditemukan.");
        }
    }

    private static void tambahSoal(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID ujian: ");
        int idUjian = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan teks soal: ");
        String teksSoal = scanner.nextLine();
        System.out.print("Masukkan skor soal: ");
        double skor = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Masukkan opsi jawaban (dipisahkan dengan koma) [Kosongkan untuk Essay]: ");
        String opsi = scanner.nextLine();
        System.out.print("Masukkan jawaban benar: ");
        String jawabanBenar = scanner.nextLine();
        System.out.print("Masukkan tipe soal (MC/Essay): ");
        String tipe = scanner.nextLine();

        PreparedStatement stmt = koneksi.prepareStatement(
                "INSERT INTO questions (exam_id, question_text, score, options, correct_answer, type) VALUES (?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, idUjian);
        stmt.setString(2, teksSoal);
        stmt.setDouble(3, skor);
        stmt.setString(4, opsi.isEmpty() ? null : opsi);
        stmt.setString(5, jawabanBenar);
        stmt.setString(6, tipe);
        stmt.executeUpdate();
        System.out.println("Soal berhasil ditambahkan!");
    }

    private static void lihatDaftarSoal(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID ujian untuk melihat soal: ");
        int idUjian = scanner.nextInt();

        PreparedStatement stmt = koneksi.prepareStatement("SELECT * FROM questions WHERE exam_id = ?");
        stmt.setInt(1, idUjian);
        ResultSet hasil = stmt.executeQuery();

        System.out.println("\n=== Daftar Soal ===");
        while (hasil.next()) {
            System.out.println("ID: " + hasil.getInt("id") + ", Teks: " + hasil.getString("question_text") + ", Skor: "
                    + hasil.getDouble("score") + ", Tipe: " + hasil.getString("type") + ", Jawaban Benar: "
                    + hasil.getString("correct_answer"));
            String opsi = hasil.getString("options");
            if (opsi != null) {
                System.out.println("Opsi: " + opsi);
            }
            System.out.println();
        }
    }

    private static void ubahSoal(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID soal yang ingin diubah: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan teks soal baru: ");
        String teksSoal = scanner.nextLine();
        System.out.print("Masukkan skor baru: ");
        double skor = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Masukkan opsi baru (dipisahkan dengan koma) [Kosongkan untuk Essay]: ");
        String opsi = scanner.nextLine();
        System.out.print("Masukkan jawaban benar baru: ");
        String jawabanBenar = scanner.nextLine();
        System.out.print("Masukkan tipe soal baru (MC/Essay): ");
        String tipe = scanner.nextLine();

        PreparedStatement stmt = koneksi.prepareStatement(
                "UPDATE questions SET question_text = ?, score = ?, options = ?, correct_answer = ?, type = ? WHERE id = ?");
        stmt.setString(1, teksSoal);
        stmt.setDouble(2, skor);
        stmt.setString(3, opsi.isEmpty() ? null : opsi);
        stmt.setString(4, jawabanBenar);
        stmt.setString(5, tipe);
        stmt.setInt(6, id);
        int barisTerubah = stmt.executeUpdate();
        if (barisTerubah > 0) {
            System.out.println("Soal berhasil diubah!");
        } else {
            System.out.println("Soal tidak ditemukan.");
        }
    }

    private static void hapusSoal(Connection koneksi, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID soal yang ingin dihapus: ");
        int id = scanner.nextInt();

        PreparedStatement stmt = koneksi.prepareStatement("DELETE FROM questions WHERE id = ?");
        stmt.setInt(1, id);
        int barisTerhapus = stmt.executeUpdate();
        if (barisTerhapus > 0) {
            System.out.println("Soal berhasil dihapus!");
        } else {
            System.out.println("Soal tidak ditemukan.");
        }
    }
}

// package admin;

// import java.sql.*;
// import java.util.Scanner;
// import utils.DatabaseConnection;

// public class AdminApp {

// public static void main(String[] args) {
// Scanner scanner = new Scanner(System.in);

// try (Connection conn = DatabaseConnection.getConnection()) {
// int choice;

// do {
// System.out.println("\n=== Admin Panel ===");
// System.out.println("1. Add Exam");
// System.out.println("2. View Exams");
// System.out.println("3. Update Exam");
// System.out.println("4. Delete Exam");
// System.out.println("5. Add Question");
// System.out.println("6. View Questions");
// System.out.println("7. Update Question");
// System.out.println("8. Delete Question");
// System.out.println("0. Exit");
// System.out.print("Enter your choice: ");
// choice = scanner.nextInt();
// scanner.nextLine();

// switch (choice) {
// case 1:
// addExam(conn, scanner);
// break;
// case 2:
// viewExams(conn);
// break;
// case 3:
// updateExam(conn, scanner);
// break;
// case 4:
// deleteExam(conn, scanner);
// break;
// case 5:
// addQuestion(conn, scanner);
// break;
// case 6:
// viewQuestions(conn, scanner);
// break;
// case 7:
// updateQuestion(conn, scanner);
// break;
// case 8:
// deleteQuestion(conn, scanner);
// break;
// case 0:
// System.out.println("Exiting Admin Panel...");
// break;
// default:
// System.out.println("Invalid choice. Please try again.");
// }
// } while (choice != 0);

// } catch (SQLException e) {
// System.out.println(e);
// }
// }

// private static void addExam(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter exam title: ");
// String title = scanner.nextLine();
// System.out.print("Enter exam password: ");
// String password = scanner.nextLine();

// PreparedStatement stmt = conn.prepareStatement("INSERT INTO exams (name,
// password) VALUES (?, ?)");
// stmt.setString(1, title);
// stmt.setString(2, password);
// stmt.executeUpdate();
// System.out.println("Exam added successfully!");
// }

// private static void viewExams(Connection conn) throws SQLException {
// Statement stmt = conn.createStatement();
// ResultSet rs = stmt.executeQuery("SELECT * FROM exams");

// System.out.println("\n=== List of Exams ===");
// while (rs.next()) {
// System.out.println("ID: " + rs.getInt("id") + ", Title: " +
// rs.getString("name") + ", Password: "
// + rs.getString("password"));
// }
// }

// private static void updateExam(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter exam ID to update: ");
// int id = scanner.nextInt();
// scanner.nextLine();

// System.out.print("Enter new exam title: ");
// String title = scanner.nextLine();
// System.out.print("Enter new exam password: ");
// String password = scanner.nextLine();

// PreparedStatement stmt = conn.prepareStatement("UPDATE exams SET name = ?,
// password = ? WHERE id = ?");
// stmt.setString(1, title);
// stmt.setString(2, password);
// stmt.setInt(3, id);
// int rowsUpdated = stmt.executeUpdate();
// if (rowsUpdated > 0) {
// System.out.println("Exam updated successfully!");
// } else {
// System.out.println("Exam not found.");
// }
// }

// private static void deleteExam(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter exam ID to delete: ");
// int id = scanner.nextInt();

// PreparedStatement stmt = conn.prepareStatement("DELETE FROM exams WHERE id =
// ?");
// stmt.setInt(1, id);
// int rowsDeleted = stmt.executeUpdate();
// if (rowsDeleted > 0) {
// System.out.println("Exam deleted successfully!");
// } else {
// System.out.println("Exam not found.");
// }
// }

// private static void addQuestion(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter exam ID: ");
// int examId = scanner.nextInt();
// scanner.nextLine();

// System.out.print("Enter question text: ");
// String Question = scanner.nextLine();
// System.out.print("Enter question score: ");
// double score = scanner.nextDouble();
// scanner.nextLine();

// System.out.print("Enter options (comma-separated) [Leave empty for Essay]:
// ");
// String options = scanner.nextLine();
// System.out.print("Enter correct answer: ");
// String correctAnswer = scanner.nextLine();
// System.out.print("Enter question type (MC/Essay): ");
// String type = scanner.nextLine();

// PreparedStatement stmt = conn.prepareStatement(
// "INSERT INTO questions (exam_id, question_text, score, options,
// correct_answer, type) VALUES (?, ?, ?, ?, ?, ?)");
// stmt.setInt(1, examId);
// stmt.setString(2, Question);
// stmt.setDouble(3, score);
// stmt.setString(4, options.isEmpty() ? null : options);
// stmt.setString(5, correctAnswer);
// stmt.setString(6, type);
// stmt.executeUpdate();
// System.out.println("Question added successfully!");
// }

// private static void viewQuestions(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter exam ID to view questions: ");
// int examId = scanner.nextInt();

// PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE
// exam_id = ?");
// stmt.setInt(1, examId);
// ResultSet rs = stmt.executeQuery();

// System.out.println("\n=== List of Questions ===");
// while (rs.next()) {
// System.out.println("ID: " + rs.getInt("id") + ", Text: " +
// rs.getString("question_text") + ", Score: "
// + rs.getDouble("score")
// + ", Type: " + rs.getString("type") + ", Correct Answer: " +
// rs.getString("correct_answer"));
// String options = rs.getString("options");
// if (options != null) {
// System.out.println("Options: " + options);
// }
// System.out.println();
// }
// }

// private static void updateQuestion(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter question ID to update: ");
// int id = scanner.nextInt();
// scanner.nextLine();

// System.out.print("Enter new question text: ");
// String Question = scanner.nextLine();
// System.out.print("Enter new score: ");
// double score = scanner.nextDouble();
// scanner.nextLine();

// System.out.print("Enter new options (comma-separated) [Leave empty for
// Essay]: ");
// String options = scanner.nextLine();
// System.out.print("Enter new correct answer: ");
// String correctAnswer = scanner.nextLine();
// System.out.print("Enter new question type (MC/Essay): ");
// String type = scanner.nextLine();

// PreparedStatement stmt = conn.prepareStatement(
// "UPDATE questions SET question_text = ?, score = ?, options = ?,
// correct_answer = ?, type = ? WHERE id = ?");
// stmt.setString(1, Question);
// stmt.setDouble(2, score);
// stmt.setString(3, options.isEmpty() ? null : options);
// stmt.setString(4, correctAnswer);
// stmt.setString(5, type);
// stmt.setInt(6, id);
// int rowsUpdated = stmt.executeUpdate();
// if (rowsUpdated > 0) {
// System.out.println("Question updated successfully!");
// } else {
// System.out.println("Question not found.");
// }
// }

// private static void deleteQuestion(Connection conn, Scanner scanner) throws
// SQLException {
// System.out.print("Enter question ID to delete: ");
// int id = scanner.nextInt();

// PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE
// id = ?");
// stmt.setInt(1, id);
// int rowsDeleted = stmt.executeUpdate();
// if (rowsDeleted > 0) {
// System.out.println("Question deleted successfully!");
// } else {
// System.out.println("Question not found.");
// }
// }
// }
