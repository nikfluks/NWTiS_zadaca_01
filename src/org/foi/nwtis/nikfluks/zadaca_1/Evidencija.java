package org.foi.nwtis.nikfluks.zadaca_1;

import java.io.Serializable;

public class Evidencija implements Serializable {

    private long ukupanBrojZahtjeva = 0;//oznakaPodatka=1
    private long brojNeispravnihZahtjeva = 0;//oznakaPodatka=2
    private long brojNedozvoljenihZahtjeva = 0;//oznakaPodatka=3
    private long brojUsjesnihZahtjeva = 0;//oznakaPodatka=4
    private long brojPrekinutihZahtjeva = 0;//oznakaPodatka=5
    private long ukupnoVrijemeRadaRadnihDretvi = 0;//oznakaPodatka=6
    private long brojObavljenihSerijalizacija = 0;//oznakaPodatka=7

    public long getUkupanBrojZahtjeva() {
        return ukupanBrojZahtjeva;
    }

    public void setUkupanBrojZahtjeva(long ukupanBrojZahtjeva) {
        this.ukupanBrojZahtjeva = ukupanBrojZahtjeva;
    }

    public long getBrojNeispravnihZahtjeva() {
        return brojNeispravnihZahtjeva;
    }

    public void setBrojNeispravnihZahtjeva(long brojNeispravnihZahtjeva) {
        this.brojNeispravnihZahtjeva = brojNeispravnihZahtjeva;
    }

    public long getBrojNedozvoljenihZahtjeva() {
        return brojNedozvoljenihZahtjeva;
    }

    public void setBrojNedozvoljenihZahtjeva(long brojNedozvoljenihZahtjeva) {
        this.brojNedozvoljenihZahtjeva = brojNedozvoljenihZahtjeva;
    }

    public long getBrojUsjesnihZahtjeva() {
        return brojUsjesnihZahtjeva;
    }

    public void setBrojUsjesnihZahtjeva(long brojUsjesnihZahtjeva) {
        this.brojUsjesnihZahtjeva = brojUsjesnihZahtjeva;
    }

    public long getBrojPrekinutihZahtjeva() {
        return brojPrekinutihZahtjeva;
    }

    public void setBrojPrekinutihZahtjeva(long brojPrekinutihZahtjeva) {
        this.brojPrekinutihZahtjeva = brojPrekinutihZahtjeva;
    }

    public long getUkupnoVrijemeRadaRadnihDretvi() {
        return ukupnoVrijemeRadaRadnihDretvi;
    }

    public void setUkupnoVrijemeRadaRadnihDretvi(long ukupnoVrijemeRadaRadnihDretvi) {
        this.ukupnoVrijemeRadaRadnihDretvi = ukupnoVrijemeRadaRadnihDretvi;
    }

    public long getBrojObavljenihSerijalizacija() {
        return brojObavljenihSerijalizacija;
    }

    public void setBrojObavljenihSerijalizacija(long brojObavljenihSerijalizacija) {
        this.brojObavljenihSerijalizacija = brojObavljenihSerijalizacija;
    }
}
