package org.foi.nwtis.nikfluks.zadaca_1;

public class AzurirajEvidenciju {

    public static long radMillis = 0;

    public AzurirajEvidenciju() {
    }

    public static synchronized void azurirajEvidenciju(int oznakaPodatka) {
        switch (oznakaPodatka) {
            case 1:
                ServerSustava.evid.setUkupanBrojZahtjeva(ServerSustava.evid.getUkupanBrojZahtjeva() + 1);
                break;
            case 2:
                ServerSustava.evid.setBrojNeispravnihZahtjeva(ServerSustava.evid.getBrojNeispravnihZahtjeva() + 1);
                break;
            case 3:
                ServerSustava.evid.setBrojNedozvoljenihZahtjeva(ServerSustava.evid.getBrojNedozvoljenihZahtjeva() + 1);
                break;
            case 4:
                ServerSustava.evid.setBrojUsjesnihZahtjeva(ServerSustava.evid.getBrojUsjesnihZahtjeva() + 1);
                break;
            case 5:
                ServerSustava.evid.setBrojPrekinutihZahtjeva(ServerSustava.evid.getBrojPrekinutihZahtjeva() + 1);
                break;
            case 6:
                ServerSustava.evid.setUkupnoVrijemeRadaRadnihDretvi(ServerSustava.evid.getUkupnoVrijemeRadaRadnihDretvi()
                        + radMillis);
                break;
            case 7:
                ServerSustava.evid.setBrojObavljenihSerijalizacija(ServerSustava.evid.getBrojObavljenihSerijalizacija() + 1);
                break;
        }
    }
}
