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