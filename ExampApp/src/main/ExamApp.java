package main;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        mainPanel = new JPanel(new BorderLayout());

        try {
            koneksi = DatabaseConnection.getConnection();
            loadExamsFromDatabase();
        } catch (SQLException e) {
            showErrorDialog("Error saat mengakses database: " + e.getMessage());
            System.exit(1);
        }

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(cardLayout);

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
        startButton.setBackground(new Color(0, 100, 30)); // Mengatur latar belakang tombol menjadi hijau
        startButton.setForeground(Color.WHITE); // Mengatur teks tombol menjadi putih 
        startButton.addActionListener(e -> startExam());
        examPanel.add(startButton);

        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.setBackground(Color.RED); // Mengatur latar belakang tombol menjadi merah
        backButton.setForeground(Color.WHITE); // Mengatur teks tombol menjadi putih
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
        System.out.println("Memulai ujian...");

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

        System.out.println("Ujian dimulai untuk examId: " + examId);

        try {

            int timeLimit = 0;
            PreparedStatement stmt = koneksi.prepareStatement("SELECT time_limit FROM exams WHERE id = ?");
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                timeLimit = rs.getInt("time_limit");
            }

            List<Pertanyaan> daftarPertanyaan = ambilPertanyaanDariDatabase(koneksi, examId);
            if (daftarPertanyaan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada soal untuk ujian ini!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            showExamWindow(daftarPertanyaan, namaLengkap, examId, timeLimit);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil soal atau waktu: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showExamWindow(List<Pertanyaan> daftarPertanyaan, String namaLengkap, int examId, int timeLimit) {
        JFrame ujianPanel = new JFrame("Ujian");
        ujianPanel.setSize(800, 600);
        ujianPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ujianPanel.setLayout(new BorderLayout());
    
        // Label untuk timer
        JLabel timerLabel = new JLabel("Waktu: " + timeLimit + " menit", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.BLACK);
        ujianPanel.add(timerLabel, BorderLayout.NORTH);
    
        // Panel untuk pertanyaan
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        ujianPanel.add(scrollPane, BorderLayout.CENTER);
    
        // Panel untuk tombol
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton finishButton = new JButton("Selesai Ujian");
        finishButton.setFont(new Font("Arial", Font.PLAIN, 16));
        finishButton.setBackground(new Color(0, 100, 30));
        finishButton.setForeground(new Color(255,255,255));
        finishButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(finishButton);
        ujianPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        // Map untuk jawaban peserta
        Map<Pertanyaan, Object> jawabanPeserta = new LinkedHashMap<>();
        int nomorSoal = 1;
        for (Pertanyaan pertanyaan : daftarPertanyaan) {
            JPanel singleQuestionPanel = new JPanel();
            singleQuestionPanel.setLayout(new BoxLayout(singleQuestionPanel, BoxLayout.Y_AXIS));
            singleQuestionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            singleQuestionPanel.setBorder(BorderFactory.createTitledBorder("Pertanyaan " + nomorSoal));
            singleQuestionPanel.setBackground(new Color(240, 240, 240));
            singleQuestionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            JLabel numberLabel = new JLabel("Soal " + nomorSoal + ":");
            numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
            singleQuestionPanel.add(numberLabel);
    
            JLabel questionLabel = new JLabel("<html>" + pertanyaan.getTeksPertanyaan() + "</html>");
            questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            singleQuestionPanel.add(questionLabel);
    
            questionPanel.add(singleQuestionPanel);
            nomorSoal++;

            if (pertanyaan instanceof PilihanGanda) {
                PilihanGanda pg = (PilihanGanda) pertanyaan;
                ButtonGroup group = new ButtonGroup();
                JPanel optionsPanel = new JPanel(new GridLayout(pg.getOpsi().length, 1));
                optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                for (String opsi : pg.getOpsi()) {
                    JRadioButton optionButton = new JRadioButton(opsi);
                    group.add(optionButton);
                    optionsPanel.add(optionButton);
                }
                jawabanPeserta.put(pertanyaan, group);
                singleQuestionPanel.add(optionsPanel);

            } else if (pertanyaan instanceof Esai) {
                JTextArea essayAnswerField = new JTextArea(3, 20);
                essayAnswerField.setLineWrap(true);
                essayAnswerField.setWrapStyleWord(true);
                JScrollPane essayScrollPane = new JScrollPane(essayAnswerField);
                essayScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
                jawabanPeserta.put(pertanyaan, essayAnswerField);
                singleQuestionPanel.add(essayScrollPane);
            }

            questionPanel.add(singleQuestionPanel);
            questionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            nomorSoal++;
        }

        Thread timerThread = new Thread(() -> {
            int timer = timeLimit * 60;
            try {
                while (timer > 0) {
                    int minutes = timer / 60;
                    int seconds = timer % 60;
                    timerLabel.setText(String.format("Waktu Tersisa: %02d:%02d", minutes, seconds));
                    Thread.sleep(1000);
                    timer--;
                }

                JOptionPane.showMessageDialog(ujianPanel, "Waktu ujian telah habis!", "Ujian Selesai",
                        JOptionPane.WARNING_MESSAGE);
                finishExam(daftarPertanyaan, jawabanPeserta, namaLengkap, examId, ujianPanel);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        timerThread.start();

        finishButton
                .addActionListener(e -> finishExam(daftarPertanyaan, jawabanPeserta, namaLengkap, examId, ujianPanel));

        ujianPanel.setLocationRelativeTo(null);
        ujianPanel.setVisible(true);
    }

    private void finishExam(List<Pertanyaan> daftarPertanyaan, Map<Pertanyaan, Object> jawabanPeserta,
            String namaLengkap, int examId, JFrame ujianPanel) {
        double skorTotal = 0;
        double skorMaksimum = 0;
        StringBuilder jawabanTeks = new StringBuilder();

        for (Pertanyaan pertanyaan : daftarPertanyaan) {
            Object jawabanComponent = jawabanPeserta.get(pertanyaan);
            String jawaban = "";

            if (pertanyaan instanceof PilihanGanda && jawabanComponent instanceof ButtonGroup) {
                ButtonGroup group = (ButtonGroup) jawabanComponent;
                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        jawaban = button.getText();
                        break;
                    }
                }
            } else if (pertanyaan instanceof Esai && jawabanComponent instanceof JTextArea) {
                JTextArea essayAnswerField = (JTextArea) jawabanComponent;
                jawaban = essayAnswerField.getText().trim();
            }

            skorMaksimum += pertanyaan.getSkor();
            jawabanTeks.append(pertanyaan.getTeksPertanyaan()).append(": ").append(jawaban).append("; ");

            if (pertanyaan.validasiJawaban(jawaban)) {
                skorTotal += pertanyaan.getSkor();
            }
        }

        double skorAkhir = (skorTotal / skorMaksimum) * 100;

        try {
            PreparedStatement stmt = koneksi.prepareStatement(
                    "INSERT INTO peserta (name, exam_id, answers, final_score) VALUES (?, ?, ?, ?)");
            stmt.setString(1, namaLengkap);
            stmt.setInt(2, examId);
            stmt.setString(3, jawabanTeks.toString().trim());
            stmt.setDouble(4, skorAkhir);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ujianPanel, "Error saat menyimpan hasil ujian: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(ujianPanel, "Ujian selesai!\nSkor Anda: " + skorAkhir + "/100", "Hasil Ujian",
                JOptionPane.INFORMATION_MESSAGE);

        ujianPanel.dispose();

        showViewResult(namaLengkap, examId);
    }

    private void showViewResult(String namaLengkap, int examId) {
        JFrame resultFrame = new JFrame("Hasil Ujian");
        resultFrame.setSize(600, 400);
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Hasil Ujian", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultFrame.add(headerLabel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel(new BorderLayout());
        String[] columnNames = { "Nama", "Exam ID", "Final Score", "Answers" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            PreparedStatement stmt = koneksi.prepareStatement(
                    "SELECT name, exam_id, final_score, answers FROM peserta WHERE name = ? AND exam_id = ?");
            stmt.setString(1, namaLengkap);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int exam = rs.getInt("exam_id");
                double score = rs.getDouble("final_score");
                String answers = rs.getString("answers");

                tableModel.addRow(new Object[] { name, exam, score, answers });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(resultFrame, "Error saat mengambil hasil: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JTable resultTable = new JTable(tableModel);
        resultTable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        resultFrame.add(resultPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.setBackground(new Color(0,100,30));
        backButton.setForeground(new Color(255,255,255));
        backButton.addActionListener(e -> {
            resultFrame.dispose();
            cardLayout.show(mainPanel, "Dashboard");
        });
        buttonPanel.add(backButton);

        resultFrame.add(buttonPanel, BorderLayout.SOUTH);

        resultFrame.setLocationRelativeTo(null);
        resultFrame.setVisible(true);
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

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
