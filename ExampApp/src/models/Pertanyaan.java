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