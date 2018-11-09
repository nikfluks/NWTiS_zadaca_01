package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nikfluks.konfiguracije.Konfiguracija;

class SerijalizatorEvidencije extends Thread {

    private String datotekaEvidencije;
    private String nazivDretve;
    private Konfiguracija konf;
    private boolean radi = true;

    SerijalizatorEvidencije(String nazivDretve, Konfiguracija konf) {
        super(nazivDretve);
        this.nazivDretve = nazivDretve;
        this.konf = konf;
    }

    @Override
    public void interrupt() {
        System.out.println("Serijalizator evidencije interuptan!");
        radi = false;
        super.interrupt();
    }

    @Override
    public void run() {
        datotekaEvidencije = konf.dajPostavku("datoteka.evidencije.rada");
        int interval = Integer.parseInt(konf.dajPostavku("interval.za.serijalizaciju"));

        try {
            while (radi) {
                long pocetakMillis = System.currentTimeMillis();

                System.out.println("Dretva: " + nazivDretve + " Pocetak: " + trenutniMillisURazumljivoVrijeme(pocetakMillis));
                serijalizirajEvidenciju();

                long krajMillis = System.currentTimeMillis();
                long radMillis = krajMillis - pocetakMillis;
                long cekaj = (interval * 1000) - radMillis;

                Thread.sleep(cekaj);
            }
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public synchronized LocalTime trenutniMillisURazumljivoVrijeme(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalTime razumljivoVrijeme = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        return razumljivoVrijeme;
    }

    public synchronized void serijalizirajEvidenciju() {
        File datEvidencija = new File(datotekaEvidencije);
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(new FileOutputStream(datEvidencija));
            oos.writeObject(ServerSustava.evid);
            AzurirajEvidenciju.azurirajEvidenciju(7);
        } catch (IOException ex) {
            Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
