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