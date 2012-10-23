package com.daedalusgames.shotgun_blob;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import java.util.HashSet;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.app.Activity;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * // -------------------------------------------------------------------------
/**
 *  The main activity for the game. Also the launcher.
 *
 *  @author Artur
 *  @version Sep 25, 2012
 */
public class Main extends Activity
{
    //Fields-----------------------------------------------------------------

    //////////
    //BOX2D
    /** The Box2D physics world. */
    private static World world = new World(new Vec2(0.0f, 10.0f), true);

    /** The Box2d meters to pixels ratio. */
    public static final float RATIO = 30.0f; // 1 meter = 30 pixels


    //////////
    //ANDROID GRAPHICS
    /** The display metrics object to get screen characteristics. */
    public static DisplayMetrics displayMetrics = new DisplayMetrics();


    //////////
    //ANDROID SENSORS
    /** The android sensor manager. */
    private static SensorManager sensorManager;

    /** The gravity sensor. */
    private static DeviceSensor gravitySensor;

    /** The gravity vector set by the sensor. */
    private static Vec2 gravity = new Vec2(0.0f, 0.0f);


    /////////
    //GAME
    /** The set containing references to all actors. */
    private static HashSet<Actor> actors = new HashSet<Actor>();

    /** The main character. */
    private static Blob blob;



    //Methods-----------------------------------------------------------------
    /**
     * This method gets executed when the activity is created.
     * It functions as a constructor.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        super.onCreate(savedInstanceState);
        setContentView(new Panel(this));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gravitySensor = new DeviceSensor(Sensor.TYPE_GRAVITY);
    }

    /**
     * This method gets called when the application looses focus.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        gravitySensor.onPause();
    }

    /**
     * This method gets called when the application regains focus.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        gravitySensor.onResume();
    }

    /**
     * The world getter.
     * @return world The JBox2d world.
     */
    public static World getWorld()
    {
        return world;
    }

    /**
     * The main character getter.
     * @return blob The main character.
     */
    public static Blob getBlob()
    {
        return blob;
    }

    /**
     * The main character setter.
     * @param newBlob The new blob instance.
     */
    public static void setBlob(Blob newBlob)
    {
        blob = newBlob;
    }

    /**
     * The gravity getter.
     * @return gravity The gravity being applied in the world.
     */
    public static Vec2 getGravity()
    {
        return gravity;
    }

    /**
     * The gravity setter.
     * @param newGravity The new value for the gravity vector.
     */
    public static void setGravity(Vec2 newGravity)
    {
        gravity = newGravity;
    }

    /**
     * The sensor manager getter.
     * @return sensorManager The android sensor manager object.
     */
    public static SensorManager getSensorManager()
    {
        return sensorManager;
    }

    /**
     * The getter for all the actors.
     * @return HashSet with all actors.
     */
    public static HashSet<Actor> getActors()
    {
        return actors;
    }

    /**
     * Adds an actor to the array list of all actors.
     * @param actor The actor to be added.
     */
    public static void pushActor(Actor actor)
    {
        actors.add(actor);
    }

}
