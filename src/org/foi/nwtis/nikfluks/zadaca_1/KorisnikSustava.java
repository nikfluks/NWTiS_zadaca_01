package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.nikfluks.konfiguracije.NemaKonfiguracije;

public class KorisnikSustava {

    private String[] args;
    private static Matcher m;
    String korisnik;
    String lozinka;
    String adresa;
    int port;
    String akcija;
    String datoteka;
    File dat;

    public static void main(String[] args) {
        KorisnikSustava ks = new KorisnikSustava();
        ks.args = args;

        if (ks.provjeriAdmina()) {
            AdministratorSustava as = new AdministratorSustava();
            as.preuzmiPostavkeAdmin();
            //ks.preuzmiPostavkeAdmin();
            as.preuzmiKontrolu();
        } else if (ks.provjeriKlijenta()) {
            KlijentSustava kls = new KlijentSustava();
            kls.preuzmiPostavkeKlijent();
            //ks.preuzmiPostavkeKlijent();
            kls.preuzmiKontrolu();
        } else {
            System.err.println("Pogrešni ulazni parametri!");
        }
    }

    protected void preuzmiPostavkeAdmin() {
        korisnik = m.group(1);
        lozinka = m.group(2);
        adresa = m.group(3);
        port = Integer.parseInt(m.group(4));
        odrediAkciju(m.group(5));
    }

    protected void preuzmiPostavkeKlijent() {
        korisnik = "";
        lozinka = "";
        adresa = m.group(1);
        port = Integer.parseInt(m.group(2));
        odrediAkciju(m.group(3));
    }

    protected void odrediAkciju(String akcijaParametar) {
        String[] akcijaParametarPodjeljeni = akcijaParametar.split(" ");

        if (akcijaParametarPodjeljeni.length == 1) {
            odrediAkcijuZa1Parametar(akcijaParametar);
        } else {//lenght = 2, akcija se sastoji od 2 parametra
            odrediAkcijuZa2Parametra(akcijaParametarPodjeljeni);
        }
    }

    protected boolean provjeriAdmina() {
        String sintaksa = "^-k ([A-ZČĆŽŠĐa-zčćžšđ0-9\\_\\-]{3,10}) "
                + "-l ([A-ZČĆŽŠĐa-zčćžšđ0-9\\_\\-\\#\\!]{3,10}) "
                + "-s ((?:(?:(?:[01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.){3}(?:[01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5]))"
                + "|(?:[A-Za-z_\\-]+\\.)*[A-Za-z_\\-]+) "
                + "-p ((?:[8][0-9][0-9][0-9])|(?:[9][0-9][0-9][0-9]))"
                + "(?: (--pauza|--kreni|--zaustavi|--stanje"
                + "|--evidencija [A-Za-z0-9_:\\/\\-\\\\]+\\.(?i)(?:txt|xml|json|bin)"
                + "|--iot [A-Za-z0-9_:\\/\\-\\\\]+\\.(?i)(?:txt|xml|json|bin)))$";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        m = pattern.matcher(p);
        boolean status = m.matches();
        return status;
    }

    protected boolean provjeriKlijenta() {
        String sintaksa = "^-s ((?:(?:(?:[01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.){3}(?:[01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5]))"
                + "|(?:[A-Za-z_\\-]+\\.)*[A-Za-z_\\-]+) "
                + "-p ((?:[8][0-9][0-9][0-9])|(?:[9][0-9][0-9][0-9]))"
                + "(?: ((?:--spavanje (?:[1-5][0-9][0-9]|[1-9][0-9]|[1-9]|600))"
                + "|(?:[A-Za-z0-9_:\\/\\-\\\\]+\\.(?i)(?:txt|xml|json|bin))))$";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        m = pattern.matcher(p);
        boolean status = m.matches();
        return status;
    }

    protected void odrediAkcijuZa1Parametar(String akcijaParametar) {
        switch (akcijaParametar) {
            case "--pauza":
                akcija = "PAUZA";
                break;
            case "--kreni":
                akcija = "KRENI";
                break;
            case "--zaustavi":
                akcija = "ZAUSTAVI";
                break;
            case "--stanje":
                akcija = "STANJE";
                break;
            //default akcija je od klijenta koji salje parametar "datoteka" koja sadrzi podatke o IOT uredaju
            default:
                odrediAkcijuZaParametarDatoteka(akcijaParametar);
                break;
        }
    }

    protected void odrediAkcijuZa2Parametra(String[] akcijaParametarPodjeljeni) {
        switch (akcijaParametarPodjeljeni[0]) {
            case "--evidencija":
                akcija = "EVIDENCIJA";
                datoteka = akcijaParametarPodjeljeni[1];
                break;
            case "--iot":
                akcija = "IOT";
                datoteka = akcijaParametarPodjeljeni[1];
                break;
            case "--spavanje":
                akcija = "CEKAJ " + akcijaParametarPodjeljeni[1];
                break;
        }
    }

    protected void odrediAkcijuZaParametarDatoteka(String putanjaDatoteke) {
        try {
            provjeriDatoteku(putanjaDatoteke);
            InputStream is = Files.newInputStream(dat.toPath(), StandardOpenOption.READ);
            StringBuilder sadrzajDatoteke = new StringBuilder();
            int znak = 0;

            while ((znak = is.read()) != -1) {
                sadrzajDatoteke.append((char) znak);
            }

            akcija = "IOT " + sadrzajDatoteke;
            is.close();
        } catch (IOException ex) {
            System.err.println("Problem kod čitanja iz datoteke: " + putanjaDatoteke);
        } catch (NemaKonfiguracije ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void provjeriDatoteku(String datoteka) throws NemaKonfiguracije {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("Datoteka mora imati naziv");
        }

        dat = new File(datoteka);

        if (!dat.exists()) {
            throw new NemaKonfiguracije("Datoteka " + datoteka + " ne postoji!");
        } else if (dat.isDirectory()) {
            throw new NemaKonfiguracije("Datoteka " + datoteka + " nije datoteka!");
        }
    }
}
