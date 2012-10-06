package com.daedalusgames.shotgun_blob;
import junit.framework.TestCase;

/**
 * // -------------------------------------------------------------------------
/**
 *  Provides test cases for the Actor class.
 *
 *  @author Artur
 *  @version Sep 24, 2012
 */
public class BlobTest extends TestCase
{
    private Blob blob;

    private int beforeBodyCount;

    /**
     * Empty constructor for tests.
     * Initialization is done in the setUp method instead.
     */
    public BlobTest()
    {
        //Empty constructor for tests.
    }

    /**
     * Initializes the fields before every test method is executed.
     */
    public void setUp()
    {
        beforeBodyCount = Main.getWorld().getBodyCount();
        blob = new Blob();
    }

    /**
     * Tests the constructor values.
     */
    public void testConstructor()
    {
        //Check to see if a body was added to the world by the constructor.
        assertEquals(beforeBodyCount + 1, Main.getWorld().getBodyCount());
    }

    /**
     * Tests the health getter and setter methods.
     */
    public void testHealth()
    {
        assertEquals(blob.getHealth(), 100);
        blob.setHealth(-5);
        assertEquals(blob.getHealth(), 100);
        blob.setHealth(101);
        assertEquals(blob.getHealth(), 100);
        blob.setHealth(75);
        assertEquals(blob.getHealth(), 75);
    }
}
