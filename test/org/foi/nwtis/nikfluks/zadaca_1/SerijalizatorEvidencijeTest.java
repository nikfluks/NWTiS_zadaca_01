package org.foi.nwtis.nikfluks.zadaca_1;

import java.time.LocalTime;
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
public class SerijalizatorEvidencijeTest {

    public SerijalizatorEvidencijeTest() {
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
     * Test of interrupt method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testInterrupt() {
        System.out.println("interrupt");
        SerijalizatorEvidencije instance = null;
        instance.interrupt();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        SerijalizatorEvidencije instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        SerijalizatorEvidencije instance = null;
        instance.start();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of trenutniMillisURazumljivoVrijeme method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testTrenutniMillisURazumljivoVrijeme() {
        System.out.println("trenutniMillisURazumljivoVrijeme");
        long millis = 0L;
        SerijalizatorEvidencije instance = null;
        LocalTime expResult = null;
        LocalTime result = instance.trenutniMillisURazumljivoVrijeme(millis);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of serijalizirajEvidenciju method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testSerijalizirajEvidenciju() {
        System.out.println("serijalizirajEvidenciju");
        SerijalizatorEvidencije instance = null;
        instance.serijalizirajEvidenciju();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
