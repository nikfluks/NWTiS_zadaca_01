package org.foi.nwtis.nikfluks.zadaca_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.nikfluks.konfiguracije.Konfiguracija;
import org.foi.nwtis.nikfluks.konfiguracije.NemaKonfiguracije;

class RadnaDretva extends Thread {

    private final Socket socket;
    private final String nazivDretve;
    private final Konfiguracija konf;
    public String odgovor;
    private Matcher m;
    private String korisnik;
    private String lozinka;
    private String akcija;
    public static boolean radi = true;
    private static boolean pauziran = false;
    public static ArrayList<RadnaDretva> listaDretviNaCekanju = new ArrayList<RadnaDretva>();
    public static Socket socketZaustavi;
    private File datEvid;
    private Evidencija evid;

    public RadnaDretva(String nazivDretve, Konfiguracija konf, Socket socket) {
        super(nazivDretve);
        this.socket = socket;
        this.nazivDretve = nazivDretve;
        this.konf = konf;
    }

    @Override
    public void interrupt() {
        System.out.println("Radna dretva interuptana!");
        super.interrupt();
    }

    @Override
    public void run() {
        try {
            long pocetakMillis = System.currentTimeMillis();
            InputStream is = socket.getInputStream();
            StringBuilder komanda = new StringBuilder();
            int znak = 0;

            while ((znak = is.read()) != -1) {
                komanda.append((char) znak);
                if (is.available() == 0) {//rjesenje za 1. zahtjev kod browsera
                    break;//jer ocito on ne zatvara socket za sobom
                }
            }

            System.out.println("Dretva: " + nazivDretve + " Komanda: " + komanda + "\n");
            analizirajKomandu(komanda.toString());

            long krajMillis = System.currentTimeMillis();
            long radMillis = krajMillis - pocetakMillis;

            AzurirajEvidenciju.radMillis = radMillis;
            AzurirajEvidenciju.azurirajEvidenciju(6);
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
        ServerSustava.brojDretvi--;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private synchronized void analizirajKomandu(String komanda) {
        if (provjeriSintaksuAdmin(komanda)) {
            if (provjeriIspravnostPodataka()) {
                odrediAkciju();
            }
        } else if (pauziran) {
            odgovor = "ERROR 11; Server je pauziran pa su dozvoljene samo administratorske komande!";
            AzurirajEvidenciju.azurirajEvidenciju(3);
        } else if (provjeriSintaksuKlijent(komanda)) {
            odrediAkciju();
        } else {
            odgovor = "ERROR 02; Sintaksa nije ispravna!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
        posaljiOdgovorKorisniku();
    }

    private synchronized void posaljiOdgovorKorisniku() {
        //odgovor je null samo kad 1. put dode naredba ZAUSTAVI
        //a ona dobiva odgovor iz klase SS u metodi ugasiServer
        if (odgovor != null) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(odgovor.getBytes());
                os.flush();
                socket.shutdownOutput();
            } catch (IOException | NullPointerException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized boolean provjeriSintaksuAdmin(String komanda) {
        String sintaksa = "^KORISNIK ([A-ZČĆŽŠĐa-zčćžšđ0-9\\_\\-]{3,10}); "
                + "LOZINKA ([A-ZČĆŽŠĐa-zčćžšđ0-9\\_\\-\\#\\!]{3,10}); "
                + "(PAUZA|KRENI|ZAUSTAVI|STANJE|EVIDENCIJA|IOT);$";

        Pattern pattern = Pattern.compile(sintaksa);
        m = pattern.matcher(komanda);
        boolean status = m.matches();
        if (status) {
            korisnik = m.group(1);
            lozinka = m.group(2);
            akcija = m.group(3);
        }
        return status;
    }

    private synchronized boolean provjeriSintaksuKlijent(String komanda) {
        String sintaksa = "^(IOT [A-Za-z0-9\\[\\]\\_\\-\\\"\\{\\}\\:\\.\\,]+"
                + "|CEKAJ (?:[1-5][0-9][0-9]|[1-9][0-9]|[1-9]|600));$";

        Pattern pattern = Pattern.compile(sintaksa);
        m = pattern.matcher(komanda);
        boolean status = m.matches();
        if (status) {
            akcija = m.group(1);
        }
        return status;
    }

    private synchronized boolean provjeriIspravnostPodataka() {
        String postavkaKorisnik = "";
        boolean postojiKorisnik = false;
        boolean ispravniPodaci = false;

        for (int i = 0; i <= 9; i++) {
            if (konf.postojiPostavka("admin." + i + "." + korisnik)) {
                postavkaKorisnik = "admin." + i + "." + korisnik;
                postojiKorisnik = true;
                break;
            }
        }

        if (postojiKorisnik) {
            if (konf.dajPostavku(postavkaKorisnik).equals(lozinka)) {
                ispravniPodaci = true;
            } else {
                odgovor = "ERROR 10; Lozinka ne odgovara!";
                AzurirajEvidenciju.azurirajEvidenciju(3);
            }
        } else {
            odgovor = "ERROR 10; Korisnik nije administrator!";
            AzurirajEvidenciju.azurirajEvidenciju(3);
        }
        return ispravniPodaci;
    }

    private synchronized void odrediAkciju() {
        //if sluzi samo da pusti odgovor OK; 2 kada je odmah nakon naredbe ZAUSTAVI dosla naredba STANJE
        //bilo koja druga naredba ce samo obavijestiti korisnika da ce se server ugasiti
        if (radi || (!radi && akcija.equals("STANJE"))) {
            switch (akcija) {
                case "PAUZA":
                    pauziraj();
                    break;
                case "KRENI":
                    pokreni();
                    break;
                case "ZAUSTAVI":
                    zaustavi();
                    break;
                case "STANJE":
                    odrediStanjeServera();
                    break;
                case "EVIDENCIJA":
                    vratiEvidencijuKorisniku();
                    break;
                case "IOT":
                    vratiPodatkeOIotUredajima();
                    break;
                //ako nije ni jedna gore navedena akcija, onda je akcija od korisnika
                default:
                    obradiKorisnickuAkciju();
                    break;
            }
        }
    }

    private synchronized void pauziraj() {
        if (!pauziran) {
            pauziran = true;
            odgovor = "OK";
            AzurirajEvidenciju.azurirajEvidenciju(4);
        } else {
            odgovor = "ERROR 11; Server je već pauziran!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized void pokreni() {
        if (pauziran) {
            pauziran = false;
            odgovor = "OK";
            AzurirajEvidenciju.azurirajEvidenciju(4);
        } else {
            odgovor = "ERROR 12; Server je već pokrenuti!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized void zaustavi() {
        //spremam socket jer odgovor saljem tek kada gasim server (u klasi SS)
        //pa moram tu spremiti socket od naredbe ZAUSTAVI kako bi mogao tamo odgovoriti s OK ili ERROR 13
        socketZaustavi = socket;
        radi = false;
    }

    private synchronized void odrediStanjeServera() {
        if (radi) {
            if (!pauziran) {
                odgovor = "OK; 0";
            } else {
                odgovor = "OK; 1";
            }
        } else {
            odgovor = "OK; 2";
        }
        AzurirajEvidenciju.azurirajEvidenciju(4);
    }

    private synchronized void vratiEvidencijuKorisniku() {
        String skupKodovaZnakova = konf.dajPostavku("skup.kodova.znakova");

        try {
            if (deserijalizirajEvidenciju()) {
                StringBuilder formatiranaEvidencija = new StringBuilder();
                formatiranaEvidencija.append("Ukupan broj zahtjeva = ").append(evid.getUkupanBrojZahtjeva())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Broj neispravnih zahtjeva = ").append(evid.getBrojNeispravnihZahtjeva())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Broj nedozvoljenih zahtjeva = ").append(evid.getBrojNedozvoljenihZahtjeva())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Broj usješnih zahtjeva = ").append(evid.getBrojUsjesnihZahtjeva())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Broj prekinutih zahtjeva = ").append(evid.getBrojPrekinutihZahtjeva())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Ukupno vrijeme rada radnih dretvi = ").append(evid.getUkupnoVrijemeRadaRadnihDretvi())
                        .append(System.lineSeparator());
                formatiranaEvidencija.append("Broj obavljenih serijalizacija = ").append(evid.getBrojObavljenihSerijalizacija());
                formatiranaEvidencija.append(System.lineSeparator()).append("*****************").append(System.lineSeparator());

                String formatiranaEvidencijaUZadanomSkupuZnakova
                        = new String(formatiranaEvidencija.toString().getBytes(), skupKodovaZnakova);

                odgovor = "OK; ZN-KODOVI " + skupKodovaZnakova + "; DUZINA "
                        + formatiranaEvidencijaUZadanomSkupuZnakova.length() + "\n"
                        + formatiranaEvidencijaUZadanomSkupuZnakova;
                AzurirajEvidenciju.azurirajEvidenciju(4);
            }
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex.getMessage());
            odgovor = "ERROR 15; Problem kod pretvaranja u zadani skup znakova!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized boolean deserijalizirajEvidenciju() {
        boolean uspjesnoDesrijalizirano = false;
        String datotekaEvidencije = konf.dajPostavku("datoteka.evidencije.rada");

        try {
            provjeriDatoteku(datotekaEvidencije);

            InputStream is = Files.newInputStream(datEvid.toPath(), StandardOpenOption.READ);
            ObjectInputStream ois = new ObjectInputStream(is);
            evid = (Evidencija) ois.readObject();
            ois.close();
            is.close();
            uspjesnoDesrijalizirano = true;
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Problem kod čitanje iz datoteke evidencije: " + datotekaEvidencije + "!");
            odgovor = "ERROR 15; " + "Problem kod čitanje iz datoteke evidencije: " + datotekaEvidencije + "!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        } catch (NemaKonfiguracije ex) {
            System.err.println(ex.getMessage());
            odgovor = "ERROR 15; " + ex.getMessage();
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
        return uspjesnoDesrijalizirano;
    }

    private synchronized void provjeriDatoteku(String datoteka) throws NemaKonfiguracije {
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

    private synchronized void vratiPodatkeOIotUredajima() {
        try {
            String skupKodovaZnakova = konf.dajPostavku("skup.kodova.znakova");
            StringBuilder formatiraniIspisIotUredaja = new StringBuilder();

            for (IOT iotUredaj : IOT.listaIotUredaja) {
                formatiraniIspisIotUredaja.append("id = ").append(iotUredaj.id).append(System.lineSeparator());
                for (String kljuc : iotUredaj.rjecnikIotUredaja.keySet()) {
                    formatiraniIspisIotUredaja.append(kljuc).append(" = ").append(iotUredaj.rjecnikIotUredaja.get(kljuc))
                            .append(System.lineSeparator());
                }
                formatiraniIspisIotUredaja.append("*****************").append(System.lineSeparator());
            }

            String formatiraniIspisIotUredajaUZadanomSkupuZnakova
                    = new String(formatiraniIspisIotUredaja.toString().getBytes(), skupKodovaZnakova);

            odgovor = "OK; ZN-KODOVI " + skupKodovaZnakova + "; DUZINA "
                    + formatiraniIspisIotUredajaUZadanomSkupuZnakova.length() + "\n"
                    + formatiraniIspisIotUredajaUZadanomSkupuZnakova;
            AzurirajEvidenciju.azurirajEvidenciju(4);
        } catch (UnsupportedEncodingException ex) {
            odgovor = "ERROR 16; Problem kod učitavanja IOT uređaja!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized void obradiKorisnickuAkciju() {
        String[] akcijaPodjeljena = akcija.split(" ");

        if (akcija.startsWith("CEKAJ")) {
            int trajanjeSpavanja = Integer.parseInt(akcijaPodjeljena[1]);

            try {
                listaDretviNaCekanju.add(this);
                sleep(trajanjeSpavanja * 1000);
                listaDretviNaCekanju.remove(this);
                odgovor = "OK;";
                AzurirajEvidenciju.azurirajEvidenciju(4);
            } catch (InterruptedException ex) {
                odgovor = "ERROR 22; Dretva prekinuta u čekanju!";
                AzurirajEvidenciju.azurirajEvidenciju(2);
            }
        } else if (akcija.startsWith("IOT")) {
            if (provjeriJesuLiPodaciUJsonFormatu(akcijaPodjeljena[1])) {
                obradiJsonPodatke(akcijaPodjeljena[1]);
            }
        } else {
            odgovor = "ERROR 02; Komanda nije dozvoljena!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized boolean provjeriJesuLiPodaciUJsonFormatu(String podaci) {
        try {
            JsonObject jsonObjekt = new JsonParser().parse(podaci).getAsJsonObject();
        } catch (JsonSyntaxException ex) {
            odgovor = "ERROR 20; Neispravan Json format!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
            return false;
        }
        return true;
    }

    private synchronized void obradiJsonPodatke(String podaci) {
        try {
            JsonObject jsonObjekt = new JsonParser().parse(podaci).getAsJsonObject();
            String trenutniId = jsonObjekt.get("id").getAsString();
            IOT iot = provjeriPostojiIotUredaj(trenutniId);

            if (iot == null) {//ako je null, uredaj s tim id-om jos ne postoji
                iot = new IOT();
                odgovor = "OK 20;";
                AzurirajEvidenciju.azurirajEvidenciju(4);
                dodajNoviUredaj(iot, jsonObjekt);
            } else {
                odgovor = "OK 21;";
                AzurirajEvidenciju.azurirajEvidenciju(4);
                azurirajPostojeciUredaj(iot, jsonObjekt);
            }
        } catch (Exception ex) {
            odgovor = "ERROR 21; Došlo je do problema tijekom učitavanja IOT uređaja!";
            AzurirajEvidenciju.azurirajEvidenciju(2);
        }
    }

    private synchronized IOT provjeriPostojiIotUredaj(String id) {
        IOT uredaj = null;
        for (IOT iot : IOT.listaIotUredaja) {
            if (iot.id.equals(id)) {
                uredaj = iot;
                break;
            }
        }
        return uredaj;
    }

    private synchronized void dodajNoviUredaj(IOT iot, JsonObject jsonObjekt) {
        for (String kljucJson : jsonObjekt.keySet()) {
            JsonElement vrijednost = jsonObjekt.get(kljucJson);
            JsonPrimitive jsonPrimitiv = vrijednost.getAsJsonPrimitive();

            if (kljucJson.equals("id")) {
                iot.id = jsonPrimitiv.getAsString();
            } else {
                unesiURijecnik(jsonPrimitiv, iot, kljucJson, vrijednost);
            }
        }
        IOT.listaIotUredaja.add(iot);
    }

    private synchronized void azurirajPostojeciUredaj(IOT iot, JsonObject jsonObjekt) {
        for (String kljucJson : jsonObjekt.keySet()) {
            JsonElement vrijednost = jsonObjekt.get(kljucJson);
            JsonPrimitive jsonPrimitiv = vrijednost.getAsJsonPrimitive();

            boolean podatakVecPostoji = false;

            for (String kljucRijecnika : iot.rjecnikIotUredaja.keySet()) {
                if (kljucJson.equals(kljucRijecnika)) {
                    unesiURijecnik(jsonPrimitiv, iot, kljucJson, vrijednost);
                    podatakVecPostoji = true;
                    break;
                }
            }

            if (!kljucJson.equals("id") && !podatakVecPostoji) {
                unesiURijecnik(jsonPrimitiv, iot, kljucJson, vrijednost);
            }
        }
    }

    private synchronized void unesiURijecnik(JsonPrimitive jsonPrimitiv, IOT iot, String kljucJson, JsonElement vrijednost) {
        if (jsonPrimitiv.isBoolean()) {
            iot.rjecnikIotUredaja.put(kljucJson, vrijednost.getAsBoolean());
        } else if (jsonPrimitiv.isNumber()) {
            iot.rjecnikIotUredaja.put(kljucJson, vrijednost.getAsNumber());
        } else if (jsonPrimitiv.isString()) {
            iot.rjecnikIotUredaja.put(kljucJson, vrijednost.getAsString());
        }
    }
}
