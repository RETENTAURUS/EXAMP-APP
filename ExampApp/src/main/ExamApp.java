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