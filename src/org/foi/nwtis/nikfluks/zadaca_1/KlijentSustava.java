package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KlijentSustava extends KorisnikSustava {

    public void preuzmiKontrolu() {
        if (akcija != null) {
            String komandaZaSlanje = akcija + ";";

            try {
                Socket socket = new Socket(adresa, port);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                os.write(komandaZaSlanje.getBytes());
                os.flush();
                socket.shutdownOutput();

                StringBuilder odgovor = new StringBuilder();
                int znak;
                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                while ((znak = in.read()) != -1) {
                    odgovor.append((char) znak);
                }

                System.out.println("Odgovor: " + odgovor.toString());
            } catch (IOException ex) {
                Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
