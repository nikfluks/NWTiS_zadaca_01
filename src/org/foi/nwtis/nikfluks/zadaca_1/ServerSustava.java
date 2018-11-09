package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.nikfluks.konfiguracije.Konfiguracija;
import org.foi.nwtis.nikfluks.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.nikfluks.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.nikfluks.konfiguracije.NemaKonfiguracije;

public class ServerSustava {

    public static int brojDretvi = 0;
    public static Evidencija evid;
    private File datEvid;
    private static Konfiguracija konf;
    private static SerijalizatorEvidencije se;
    private String odgovor;
    private Socket socket;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Krivi broj argumenata!");
            return;
        }

        try {
            String datoteka = args[0];
            ServerSustava ss = new ServerSustava();
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);

            if (provjeriKonfiguraciju()) {
                ss.deserijalizirajIliKreirajEvidenciju();
                ss.pokreniPosluzitelj();
            } else {
                System.err.println("Postoji pogrešna postavka u datoteci: " + datoteka);
                return;
            }
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex.getMessage());
            return;
        }
    }

    private static boolean provjeriKonfiguraciju() {
        String port = konf.dajPostavku("port");
        String datotekaEvidencije = konf.dajPostavku("datoteka.evidencije.rada");
        String skupKodovaZnakova = konf.dajPostavku("skup.kodova.znakova");

        boolean dobraKonfiguracija = false;
        //TODO System.out.println("podrzani: " + Charset.isSupported(skupKodovaZnakova));

        if (provjeriPostavku(port, "^((?:[8][0-9][0-9][0-9])|(?:[9][0-9][0-9][0-9]))$")) {
            if (provjeriPostavku(datotekaEvidencije, "^[A-Za-z0-9_:\\/\\-\\\\]+\\.(?i)(?:txt|xml|json|bin)$")) {
                if (provjeriPostavku(skupKodovaZnakova, "^(UTF-8|ISO-8859-1|windows-1250)$")) {
                    dobraKonfiguracija = true;
                }
            }
        }
        return dobraKonfiguracija;
    }

    private static boolean provjeriPostavku(String postavka, String sintaksa) {
        String p = postavka.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        return status;
    }

    private void pokreniPosluzitelj() {
        slusacZaGasenjeServera();
        int port = Integer.parseInt(konf.dajPostavku("port"));
        int maksCekanje = Integer.parseInt(konf.dajPostavku("maks.broj.zahtjeva.cekanje"));
        int maksBrojDretvi = Integer.parseInt(konf.dajPostavku("maks.broj.radnih.dretvi"));

        try {
            ServerSocket serverSocket = new ServerSocket(port, maksCekanje);
            while (RadnaDretva.radi) {
                socket = serverSocket.accept();
                AzurirajEvidenciju.azurirajEvidenciju(1);

                System.out.println("\nKorisnik se spojio!");
                if (brojDretvi == maksBrojDretvi) {
                    odgovor = "ERROR 01; Nema slobodne radne dretve!";
                    posaljiOdgovorKorisniku(socket);
                    AzurirajEvidenciju.azurirajEvidenciju(5);
                } else {
                    if (brojDretvi >= 63) {
                        brojDretvi = 0;
                    } else {
                        brojDretvi++;
                    }
                    RadnaDretva radnaDretva = new RadnaDretva("nikfluks - " + brojDretvi, konf, socket);
                    radnaDretva.start();
                }
                //ako ovdje dretva ne spava, komanda ZAUSTAVI nece proci prvi puta jer ce se RadnaDretva.radi promjeniti na false
                //nakon sto ova dretva vec procita true te ona ceka sljedeci zahtjev nakon kojeg ce se onda stvarno zaustaviti
                //AKO se sleep makne moze se dobiti odgovor OK; 2 ako je dosla komanda STANJE
                Thread.sleep(20);
            }
            ugasiServer();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ugasiServer() {
        try {
            while (brojDretvi > 0) {//ceka da zavrse sve radne dretve
                ///prekida sve dretve koje spavaju
                for (Iterator<RadnaDretva> it = RadnaDretva.listaDretviNaCekanju.iterator(); it.hasNext();) {
                    RadnaDretva rd = it.next();
                    if (rd != null && rd.isAlive() && !rd.isInterrupted()) {
                        //s interaptom zbudim dretvom iz spavanja i brisem ju iz liste
                        rd.interrupt();
                        it.remove();
                    }
                }
            }

            odgovor = "OK";
            posaljiOdgovorKorisniku(RadnaDretva.socketZaustavi);
            AzurirajEvidenciju.azurirajEvidenciju(4);
            Runtime.getRuntime().exit(0);//tu se zapravo poziva slusacZaGasenjeServera jer on lovi shutdown event
            //i tam se obavi serijalizacija
        } catch (Exception ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            odgovor = "ERROR 13; Problem kod prekida rada ili serijalizacije!";
            posaljiOdgovorKorisniku(RadnaDretva.socketZaustavi);
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private void posaljiOdgovorKorisniku(Socket socket) {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(odgovor.getBytes());
            os.flush();
            socket.shutdownOutput();
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void provjeriDatotekuEvidencije(String datoteka) throws NemaKonfiguracije {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("Datoteka mora imati naziv");
        }

        datEvid = new File(datoteka);

        if (!datEvid.exists()) {
            throw new NemaKonfiguracije("Datoteka " + datoteka + " ne postoji!");
        } else if (datEvid.isDirectory()) {
            throw new NemaKonfiguracije("Datoteka " + datoteka + " nije datoteka!");
        }
    }

    private void deserijalizirajIliKreirajEvidenciju() {
        String datotekaEvidencije = konf.dajPostavku("datoteka.evidencije.rada");

        try {
            provjeriDatotekuEvidencije(datotekaEvidencije);

            InputStream is = Files.newInputStream(datEvid.toPath(), StandardOpenOption.READ);
            ObjectInputStream ois = new ObjectInputStream(is);
            evid = (Evidencija) ois.readObject();
            ois.close();
            is.close();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Problem kod čitanje iz datoteke evidencije: " + datotekaEvidencije + "!");
        } catch (NemaKonfiguracije ex) {
            System.err.println(ex.getMessage() + "\nDatoteka " + datotekaEvidencije + " kreirana!");
        }

        if (evid == null) {
            evid = new Evidencija();
        }

        se = new SerijalizatorEvidencije("nikfluks - serijalizator", konf);
        se.start();
    }

    private void slusacZaGasenjeServera() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Prisilno zaustavljeno s Ctrl+c, komandom ZAUSTAVI ili na neki treći nacin, gasim server...");
                se.interrupt();
                se.serijalizirajEvidenciju();
            }
        });
    }
}
