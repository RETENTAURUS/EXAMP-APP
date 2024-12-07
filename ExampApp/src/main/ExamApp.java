package main;

import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import models.*;
import utils.DatabaseConnection;

public class ExamApp extends JFrame {
    private Connection koneksi;
    private List<String> daftarUjian = new ArrayList<>();
    private Map<Integer, String> passwordUjian = new HashMap<>();
    private JComboBox<String> ujianComboBox;
    private JTextField namaField;
    private JPasswordField passwordField;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ExamApp() {
        setTitle("Exam App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        cardLayout = new CardLayout();

        // Panel utama dengan BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        try {
            koneksi = DatabaseConnection.getConnection();
            loadExamsFromDatabase();
        } catch (SQLException e) {
            showErrorDialog("Error saat mengakses database: " + e.getMessage());
            System.exit(1);
        }

        // Menambahkan panel header dan konten utama
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(cardLayout);

        // Tambahkan Dashboard Panel
        contentPanel.add(new Dashboard(cardLayout, contentPanel), "Dashboard");
        contentPanel.add(createExamSelectionPanel(), "ExamSelection");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        cardLayout.show(contentPanel, "Dashboard");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setPreferredSize(new Dimension(600, 100));

        JLabel titleLabel = new JLabel("Welcome to Exam App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createExamSelectionPanel() {
        JPanel examPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        examPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        examPanel.add(new JLabel("Pilih Ujian:", SwingConstants.CENTER));
        ujianComboBox = new JComboBox<>(daftarUjian.toArray(new String[0]));
        examPanel.add(ujianComboBox);

        examPanel.add(new JLabel("Nama Lengkap:"));
        namaField = new JTextField();
        examPanel.add(namaField);

        examPanel.add(new JLabel("Password Ujian:"));
        passwordField = new JPasswordField();
        examPanel.add(passwordField);

        JButton startButton = new JButton("Mulai Ujian");
        startButton.addActionListener(e -> startExam());
        examPanel.add(startButton);

        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.addActionListener(e -> cardLayout.show((Container) mainPanel.getComponent(1), "Dashboard"));
        examPanel.add(backButton);

        return examPanel;
    }

    private void loadExamsFromDatabase() throws SQLException {
        Statement stmt = koneksi.createStatement();
        ResultSet hasil = stmt.executeQuery("SELECT id, name, password FROM exams");

        while (hasil.next()) {
            daftarUjian.add(hasil.getString("name"));
            passwordUjian.put(hasil.getInt("id"), hasil.getString("password"));
        }
    }

    private void startExam() {
        int selectedExamIndex = ujianComboBox.getSelectedIndex();
        String selectedExam = daftarUjian.get(selectedExamIndex);
        int examId = selectedExamIndex + 1;
        String examPassword = passwordUjian.get(examId);
        String namaLengkap = namaField.getText();
        String password = new String(passwordField.getPassword());

        if (namaLengkap.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan password harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(examPassword)) {
            JOptionPane.showMessageDialog(this, "Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load questions and start the quiz
        try {
            List<Pertanyaan> daftarPertanyaan = ambilPertanyaanDariDatabase(koneksi, examId);
            startQuiz(daftarPertanyaan, namaLengkap, examId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Pertanyaan> ambilPertanyaanDariDatabase(Connection koneksi, int idUjian) throws SQLException {
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

    private void startQuiz(List<Pertanyaan> daftarPertanyaan, String namaLengkap, int examId) {
        double skorTotal = 0;
        double skorMaksimum = 0;

        for (Pertanyaan pertanyaan : daftarPertanyaan) {
            String jawaban = JOptionPane.showInputDialog(this, pertanyaan.getTeksPertanyaan(), "Soal Ujian", JOptionPane.QUESTION_MESSAGE);
            skorMaksimum += pertanyaan.getSkor();
            if (pertanyaan.validasiJawaban(jawaban)) {
                skorTotal += pertanyaan.getSkor();
            }
        }

        double skorAkhir = (skorTotal / skorMaksimum) * 100;
        JOptionPane.showMessageDialog(this, "Ujian selesai!\nSkor Anda: " + skorAkhir + "/100", "Hasil Ujian", JOptionPane.INFORMATION_MESSAGE);
        
        // Simpan skor ke database
        try {
            PreparedStatement stmt = koneksi.prepareStatement(
                    "INSERT INTO users (name, exam_id, answers, final_score) VALUES (?, ?, ?, ?)");
            stmt.setString(1, namaLengkap);
            stmt.setInt(2, examId);
            stmt.setString(3, "{}"); // Placeholder jawaban
            stmt.setDouble(4, skorAkhir);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    
}
