package org.foi.nwtis.nikfluks.zadaca_1;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nikola
 */
public class KorisnikSustavaTest {

    public KorisnikSustavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class KorisnikSustava.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        KorisnikSustava.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of preuzmiPostavkeAdmin method, of class KorisnikSustava.
     */
    @Test
    public void testPreuzmiPostavkeAdmin() {
        System.out.println("preuzmiPostavkeAdmin");
        KorisnikSustava instance = new KorisnikSustava();
        instance.preuzmiPostavkeAdmin();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of preuzmiPostavkeKlijent method, of class KorisnikSustava.
     */
    @Test
    public void testPreuzmiPostavkeKlijent() {
        System.out.println("preuzmiPostavkeKlijent");
        KorisnikSustava instance = new KorisnikSustava();
        instance.preuzmiPostavkeKlijent();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of odrediAkciju method, of class KorisnikSustava.
     */
    @Test
    public void testOdrediAkciju() {
        System.out.println("odrediAkciju");
        String akcijaParametar = "";
        KorisnikSustava instance = new KorisnikSustava();
        instance.odrediAkciju(akcijaParametar);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of provjeriAdmina method, of class KorisnikSustava.
     */
    @Test
    public void testProvjeriAdmina() {
        System.out.println("provjeriAdmina");
        KorisnikSustava instance = new KorisnikSustava();
        boolean expResult = false;
        boolean result = instance.provjeriAdmina();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of provjeriKlijenta method, of class KorisnikSustava.
     */
    @Test
    public void testProvjeriKlijenta() {
        System.out.println("provjeriKlijenta");
        KorisnikSustava instance = new KorisnikSustava();
        boolean expResult = false;
        boolean result = instance.provjeriKlijenta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of odrediAkcijuZa1Parametar method, of class KorisnikSustava.
     */
    @Test
    public void testOdrediAkcijuZa1Parametar() {
        System.out.println("odrediAkcijuZa1Parametar");
        String akcijaParametar = "";
        KorisnikSustava instance = new KorisnikSustava();
        instance.odrediAkcijuZa1Parametar(akcijaParametar);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of odrediAkcijuZa2Parametra method, of class KorisnikSustava.
     */
    @Test
    public void testOdrediAkcijuZa2Parametra() {
        System.out.println("odrediAkcijuZa2Parametra");
        String[] akcijaParametarPodjeljeni = null;
        KorisnikSustava instance = new KorisnikSustava();
        instance.odrediAkcijuZa2Parametra(akcijaParametarPodjeljeni);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of odrediAkcijuZaParametarDatoteka method, of class KorisnikSustava.
     */
    @Test
    public void testOdrediAkcijuZaParametarDatoteka() {
        System.out.println("odrediAkcijuZaParametarDatoteka");
        String putanjaDatoteke = "";
        KorisnikSustava instance = new KorisnikSustava();
        instance.odrediAkcijuZaParametarDatoteka(putanjaDatoteke);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of provjeriDatoteku method, of class KorisnikSustava.
     */
    @Test
    public void testProvjeriDatoteku() throws Exception {
        System.out.println("provjeriDatoteku");
        String datoteka = "";
        KorisnikSustava instance = new KorisnikSustava();
        instance.provjeriDatoteku(datoteka);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
