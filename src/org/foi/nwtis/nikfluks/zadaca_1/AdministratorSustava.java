package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministratorSustava extends KorisnikSustava {

    StringBuilder odgovor;

    public void preuzmiKontrolu() {
        if (akcija != null) {
            String komandaZaSlanje = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; " + akcija + ";";

            try {
                Socket socket = new Socket(adresa, port);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                os.write(komandaZaSlanje.getBytes());
                os.flush();
                socket.shutdownOutput();

                odgovor = new StringBuilder();
                int znak;

                //ako nije UTF-8 hrvatske znakove ne interpretira dobro kad dodu preko soketa
                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                while ((znak = in.read()) != -1) {
                    odgovor.append((char) znak);
                }

                System.out.println("Odgovor: " + odgovor);
                analizirajOdgovor();

                in.close();
            } catch (IOException ex) {
                Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void analizirajOdgovor() {
        if (akcija.equals("EVIDENCIJA") || akcija.equals("IOT")) {
            //odgovorPodjeljeni ima 3 dijela: status, kodovi znakova, duzina+evidencija/iot
            String[] odgovorPodjeljeni = odgovor.toString().split(";");
            String status = odgovorPodjeljeni[0];

            if (status.equals("OK")) {
                zapisiUDatoteku(odgovorPodjeljeni);
            }
        }
    }

    private void zapisiUDatoteku(String[] odgovorPodjeljeni) {

        try {
            String[] odgSkupKodovaZnakova = odgovorPodjeljeni[1].split(" ");
            String skupKodovaZnakova = odgSkupKodovaZnakova[2];
            //iz 3. dijela uzmemo prvi \n i uzmemo string od tog znaka pa do kraja
            String odg = odgovorPodjeljeni[2].substring(odgovorPodjeljeni[2].indexOf("\n") + 1);

            dat = new File(datoteka);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dat), skupKodovaZnakova));

            bw.write(odg);
            bw.close();
            System.out.println("Odgovor zapisan u datoteku: " + dat.getPath());
        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
