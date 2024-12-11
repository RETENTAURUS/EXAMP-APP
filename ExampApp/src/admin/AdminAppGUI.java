package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

import utils.DatabaseConnection;

public class AdminAppGUI extends JFrame {
    private Connection koneksi;

    public AdminAppGUI() {
        setTitle("ADMIN PANEL");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Pusatkan jendela
        setLayout(new BorderLayout());

        // Warna utama dan aksen
        Color primaryColor = new Color(33, 150, 243); // Biru
        Color accentColor = new Color(255, 255, 255); // Putih

        // Header
        JLabel imageLabel = new JLabel(new ImageIcon("ExampApp/icon/logo2.png"));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Gunakan FlowLayout
        headerPanel.add(imageLabel);
        JLabel headerLabel = new JLabel();
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Mencoba menghubungkan ke database
        try {
            koneksi = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));

        // Tombol dengan action listener
        JButton tambahUjianButton = createStyledButton("TAMBAH UJIAN", primaryColor, accentColor, e -> tambahUjian());
        JButton lihatUjianButton = createStyledButton("LIHAT DAFTAR UJIAN", primaryColor, accentColor, e -> lihatDaftarUjian());
        JButton ubahUjianButton = createStyledButton("UBAH UJIAN", primaryColor, accentColor, e -> ubahUjian());
        JButton hapusUjianButton = createStyledButton("HAPUS UJIAN", primaryColor, accentColor, e -> hapusUjian());
        JButton tambahSoalButton = createStyledButton("TAMBAH SOAL", primaryColor, accentColor, e -> tambahSoal());
        JButton lihatSoalButton = createStyledButton("LIHAT DAFTAR SOAL", primaryColor, accentColor, e -> lihatDaftarSoal());
        JButton ubahSoalButton = createStyledButton("UBAH SOAL", primaryColor, accentColor, e -> ubahSoal());
        JButton hapusSoalButton = createStyledButton("HAPUS SOAL", primaryColor, accentColor, e -> hapusSoal());

        menuPanel.add(tambahUjianButton);
        menuPanel.add(lihatUjianButton);
        menuPanel.add(ubahUjianButton);
        menuPanel.add(hapusUjianButton);
        menuPanel.add(tambahSoalButton);
        menuPanel.add(lihatSoalButton);
        menuPanel.add(ubahSoalButton);
        menuPanel.add(hapusSoalButton);

        add(menuPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Admin Panel © 2024", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);

        // Set Visible
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color background, Color foreground, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 2),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(actionListener);
        button.setContentAreaFilled(false); // Menghilangkan area isi tombol
        button.setOpaque(true); // Mengaktifkan pengisian latar belakang tombol
        button.setBackground(background);
        button.setForeground(foreground);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(background.brighter());
                button.setForeground(new Color(255, 255, 255));
            }
    
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
                button.setForeground(foreground);
            }
        });
        return button;
    }

    private void tambahUjian() {
        // Membuat JFrame untuk dialog tambah ujian
        JFrame frame = new JFrame("Tambah Ujian");
        frame.setSize(400, 200);
        frame.setLayout(null); // Menggunakan layout absolute
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        // Membuat label dan field untuk input judul ujian
        JLabel judulLabel = new JLabel("Judul Ujian:");
        judulLabel.setBounds(30, 20, 100, 25); // Posisi dan ukuran
        JTextField judulField = new JTextField();
        judulField.setBounds(140, 20, 200, 25);
    
        // Membuat label dan field untuk input password ujian
        JLabel passwordLabel = new JLabel("Password Ujian:");
        passwordLabel.setBounds(30, 60, 100, 25);
        JTextField passwordField = new JTextField();
        passwordField.setBounds(140, 60, 200, 25);
    
        // Membuat tombol tambah
        JButton submitButton = new JButton("Tambah");
        submitButton.setBounds(140, 100, 90, 30);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(33, 150, 243));
    
        // Membuat tombol batal
        JButton cancelButton = new JButton("Batal");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(33, 150, 243));
        cancelButton.setBounds(250, 100, 90, 30);
    
        // Menambahkan komponen ke JFrame
        frame.add(judulLabel);
        frame.add(judulField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(submitButton);
        frame.add(cancelButton);
    
        // Menambahkan aksi pada tombol Tambah
        submitButton.addActionListener(e -> {
            try {
                String judul = judulField.getText();
                String password = passwordField.getText();
    
                if (judul.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Semua kolom harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
    
                PreparedStatement stmt = koneksi.prepareStatement("INSERT INTO exams (name, password) VALUES (?, ?)");
                stmt.setString(1, judul);
                stmt.setString(2, password);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Ujian berhasil ditambahkan!");
                frame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // Menambahkan aksi pada tombol Batal
        cancelButton.addActionListener(e -> frame.dispose());
    
        // Menampilkan frame di tengah layar
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }    
    
    private void lihatDaftarUjian() {
    try {
        Statement stmt = koneksi.createStatement();
        ResultSet hasil = stmt.executeQuery("SELECT * FROM exams");

        // Membuat model tabel untuk menyimpan data
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Judul", "Password"}, 0);

        // Mengisi tabel dengan data dari ResultSet
        while (hasil.next()) {
            model.addRow(new Object[]{
                    hasil.getInt("id"),
                    hasil.getString("name"),
                    hasil.getString("password")
            });
        }

        // Membuat JTable untuk menampilkan data
        JTable tabel = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabel);

        // Menampilkan JTable dalam dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Daftar Ujian", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void ubahUjian() {
        // Membuat frame utama
        JFrame frame = new JFrame("Ubah Ujian");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    
        // Panel utama dengan GridLayout
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 baris, 2 kolom dengan jarak antar komponen
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding untuk panel
    
        // Komponen input
        JLabel idLabel = new JLabel("ID Ujian:");
        JTextField idField = new JTextField();
    
        JLabel judulLabel = new JLabel("Judul Baru:");
        JTextField judulField = new JTextField();
    
        JLabel passwordLabel = new JLabel("Password Baru:");
        JTextField passwordField = new JTextField();
    
        // Menambahkan komponen ke panel
        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(judulLabel);
        formPanel.add(judulField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
    
        // Tombol submit
        JButton submitButton = new JButton("Ubah");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.addActionListener(e -> {
            String idText = idField.getText().trim();
            String judul = judulField.getText().trim();
            String password = passwordField.getText().trim();
    
            if (idText.isEmpty() || judul.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                int id = Integer.parseInt(idText);
    
                PreparedStatement stmt = koneksi.prepareStatement("UPDATE exams SET name = ?, password = ? WHERE id = ?");
                stmt.setString(1, judul);
                stmt.setString(2, password);
                stmt.setInt(3, id);
    
                int barisTerubah = stmt.executeUpdate();
                if (barisTerubah > 0) {
                    JOptionPane.showMessageDialog(frame, "Ujian berhasil diubah!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Ujian tidak ditemukan.");
                }
                frame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Format ID tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // Panel untuk tombol
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
    
        // Menambahkan panel ke frame
        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    
        // Menampilkan frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    

    private void hapusUjian() {
        JFrame frame = new JFrame("Hapus Ujian");
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        frame.setLocationRelativeTo(null);

        JTextField idField = new JTextField(10);
        frame.add(new JLabel("ID Ujian:"));
        frame.add(idField);

        JButton submitButton = new JButton("Hapus");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());

                PreparedStatement stmt = koneksi.prepareStatement("DELETE FROM exams WHERE id = ?");
                stmt.setInt(1, id);
                int barisTerhapus = stmt.executeUpdate();
                if (barisTerhapus > 0) {
                    JOptionPane.showMessageDialog(frame, "Ujian berhasil dihapus!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Ujian tidak ditemukan.");
                }
                frame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Format ID tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(submitButton);
        frame.setVisible(true);
    }

    private void tambahSoal() {
    // Membuat JFrame untuk dialog tambah soal
    JFrame frame = new JFrame("Tambah Soal");
    frame.setSize(600, 450);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    // Menggunakan GridLayout untuk panel
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2, 10, 10)); // 0 rows, 2 columns, 10 pixels horizontal and vertical gaps
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
    
    // Membuat dan menambahkan komponen
    String[] labels = {"ID Ujian:", "Teks Soal:", "Skor:", 
                       "Opsi (Kosongkan untuk Essay):", "Jawaban Benar:", "Tipe Soal (MC/Essay):"};
    
    JTextField[] fields = new JTextField[labels.length];
    
    for (int i = 0; i < labels.length; i++) {
        panel.add(new JLabel(labels[i])); // Add label
        fields[i] = new JTextField(20); // Create and add text field
        panel.add(fields[i]);
    }

    // Membuat tombol Tambah
    JButton submitButton = new JButton("Tambah");
    submitButton.setForeground(Color.WHITE);
    submitButton.setBackground(new Color(33, 150, 243));
    panel.add(submitButton);
    
    // Membuat tombol Batal
    JButton cancelButton = new JButton("Batal");
    cancelButton.setForeground(Color.WHITE);
    cancelButton.setBackground(new Color(33, 150, 243));
    panel.add(cancelButton);
    
    // Menambahkan aksi untuk tombol Tambah
    submitButton.addActionListener(e -> {
        try {
            int idUjian = Integer.parseInt(fields[0].getText());
            String teksSoal = fields[1].getText();
            double skor = Double.parseDouble(fields[2].getText());
            String opsi = fields[3].getText();
            String jawabanBenar = fields[4].getText();
            String tipe = fields[5].getText();
    
            // Validasi input
            if (teksSoal.isEmpty() || jawabanBenar.isEmpty() || tipe.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Semua kolom wajib diisi, kecuali opsi (untuk soal essay).", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            PreparedStatement stmt = koneksi.prepareStatement(
                    "INSERT INTO questions (exam_id, question_text, score, options, correct_answer, type) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, idUjian);
            stmt.setString(2, teksSoal);
            stmt.setDouble(3, skor);
            stmt.setString(4, opsi.isEmpty() ? null : opsi);
            stmt.setString(5, jawabanBenar);
            stmt.setString(6, tipe);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Soal berhasil ditambahkan!");
            frame.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Format input tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    // Menambahkan aksi untuk tombol Batal
    cancelButton.addActionListener(e -> frame.dispose());
    
    // Menampilkan panel dan frame
    frame.add(panel);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}
    

private void lihatDaftarSoal() {
    JFrame frame = new JFrame("Lihat Daftar Soal");
    frame.setSize(600, 400);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLayout(new FlowLayout());

    JTextField idUjianField = new JTextField(10);
    JButton submitButton = new JButton("Lihat Soal");
    submitButton.setForeground(Color.WHITE);
    submitButton.setBackground(new Color(33, 150, 243));
    
    // Column names for the table
    String[] columnNames = {"ID", "Teks Soal", "Skor", "Tipe", "Jawaban Benar", "Opsi"};
    
    // Create a table with an empty model
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(580, 300)); // Set preferred size for the scroll pane

    frame.add(new JLabel("ID Ujian:"));
    frame.add(idUjianField);
    frame.add(submitButton);
    frame.add(scrollPane);

    submitButton.addActionListener(e -> {
        try {
            int idUjian = Integer.parseInt(idUjianField.getText());

            PreparedStatement stmt = koneksi.prepareStatement("SELECT * FROM questions WHERE exam_id = ?");
            stmt.setInt(1, idUjian);
            ResultSet hasil = stmt.executeQuery();

            // Clear previous data in the table model
            tableModel.setRowCount(0);

            while (hasil.next()) {
                // Retrieve data from the result set
                int idSoal = hasil.getInt("id");
                String teksSoal = hasil.getString("question_text");
                double skor = hasil.getDouble("score");
                String tipe = hasil.getString("type");
                String jawabanBenar = hasil.getString("correct_answer");
                String opsi = hasil.getString("options");

                // Add a row to the table model
                tableModel.addRow(new Object[]{
                    idSoal, teksSoal, skor, tipe, jawabanBenar, opsi != null ? opsi : ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Format input tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    frame.setLocationRelativeTo(null); // Center on screen
    frame.setVisible(true);
}

    private void ubahSoal() {
        // Membuat JFrame untuk dialog ubah soal
        JFrame frame = new JFrame("Ubah Soal");
        frame.setSize(600, 450); // Set frame size to 600x450
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Menggunakan GridLayout untuk panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10)); // 0 rows, 2 columns, 10 pixels horizontal and vertical gaps
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
    
        // Membuat dan menambahkan komponen
        String[] labels = {"ID Soal:", "Teks Soal Baru:", "Skor Baru:", 
                           "Opsi Baru (Kosongkan untuk Essay):", "Jawaban Benar Baru:", "Tipe Soal Baru (MC/Essay):"};
        
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i])); // Add label
            fields[i] = new JTextField(20); // Create and add text field
            panel.add(fields[i]);
        }
    
        // Membuat tombol Ubah
        JButton submitButton = new JButton("Ubah");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(33, 150, 243));
        panel.add(submitButton);
        
        // Membuat tombol Batal
        JButton cancelButton = new JButton("Batal");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(33, 150, 243));
        panel.add(cancelButton);
        
        // Menambahkan aksi untuk tombol Ubah
        submitButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(fields[0].getText());
                String teksSoal = fields[1].getText();
                double skor = Double.parseDouble(fields[2].getText());
                String opsi = fields[3].getText();
                String jawabanBenar = fields[4].getText();
                String tipe = fields[5].getText();
    
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
                    JOptionPane.showMessageDialog(frame, "Soal berhasil diubah!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Soal tidak ditemukan.");
                }
                frame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Format input tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Menambahkan aksi untuk tombol Batal
        cancelButton.addActionListener(e -> frame.dispose());
        
        // Menampilkan panel dan frame
        frame.add(panel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }
    
    private void hapusSoal() {
        JFrame frame = new JFrame("Hapus Soal");
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        frame.setLocationRelativeTo(null);


        JTextField idField = new JTextField(10);
        frame.add(new JLabel("ID Soal:"));
        frame.add(idField);

        JButton submitButton = new JButton("Hapus");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());

                PreparedStatement stmt = koneksi.prepareStatement("DELETE FROM questions WHERE id = ?");
                stmt.setInt(1, id);
                int barisTerhapus = stmt.executeUpdate();
                if (barisTerhapus > 0) {
                    JOptionPane.showMessageDialog(frame, "Soal berhasil dihapus!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Soal tidak ditemukan.");
                }
                frame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Format ID tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(submitButton);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminAppGUI::new);
    }
}