package com.daedalusgames.shotgun_blob;
import org.jbox2d.dynamics.World;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import org.jbox2d.common.Vec2;
import junit.framework.TestCase;

/**
 * // -------------------------------------------------------------------------
/**
 *  Provides test cases for the Actor class.
 *
 *  This is currently not working!!!
 *
 *  @author Artur
 *  @version Sep 24, 2012
 */
public class BlobTest extends TestCase
{
    private Blob blob;

    private GameWorld gameWorld;

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
        this.gameWorld = new GameWorld(null, new DisplayMetrics());
        this.gameWorld.setWorld(new World(new Vec2(0.0f, -10.0f), true));

        blob = new Blob(gameWorld);
    }

    /**
     * Tests the constructor values.
     */
    public void testConstructor()
    {
        //Check to see the correct amount of bodies were added to the world by the constructor.
        assertEquals(13, gameWorld.getWorld().getBodyCount());
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
