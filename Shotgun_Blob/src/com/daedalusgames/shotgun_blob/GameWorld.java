package com.daedalusgames.shotgun_blob;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import org.jbox2d.dynamics.Body;
import android.util.Log;
import android.content.res.Resources;
import java.util.HashSet;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * // -------------------------------------------------------------------------
/**
 *  The game world that contains all of the pertinent game information.
 *  This includes the actors.
 *  Wraps a Box2D World object.
 *
 *  @author Artur
 *  @version Oct 24, 2012
 */
public class GameWorld implements SensorEventListener
{
    //////////
    //BOX2D
    /** The Box2D world. */
    private World world;

    /** The pixels to meters ratio. */
    private float ratio;

    /** Track the Box2DThread FPS. */
    private float fps;

    /** The contact listener. */
    private ContactListener contactListener;

    /** Bodies that need to be removed safely. */
    private HashSet<Body> toBeDeleted;


    //////////
    //ANDROID GRAPHICS
    /** The display metrics object to get screen characteristics. */
    private DisplayMetrics displayMetrics;

    /** The project resources object */
    private Resources resources;


    //////////
    //ANDROID SENSORS
    /** The android sensor manager. */
    private SensorManager sensorManager;

    /** The gravity sensor. */
    private Sensor gravitySensor;

    /** The gravity vector set by the sensor. */
    private Vec2 gravity = new Vec2(0.0f, 0.0f);


    /////////
    //GAME
    /** The set containing references to all actors. */
    private HashSet<Actor> actors;

    /** The set containing actors that still have no bodies. */
    private HashSet<Actor> toBeCreated;

    /** The main character. */
    private Blob blob;

    /** The current level. */
    private Level level;

    /** Speech bubbles currently in display. */
    private HashSet<SpeechBubble> speechBubbles;


    /**
     * The game world constructor.
     * @param gravity The gravity vector for the box2d world.
     * @param resources The application resources reference.
     * @param metrics The display metrics of the screen.
     */
    public GameWorld(Resources resources, DisplayMetrics metrics)
    {
        this.resources = resources;

        displayMetrics = metrics;

        ratio = 20.0f * displayMetrics.density;

        toBeDeleted = new HashSet<Body>();

        contactListener = new ContactListener(this);

        actors = new HashSet<Actor>();
        toBeCreated = new HashSet<Actor>();

        level = new Level(this);

        speechBubbles = new HashSet<SpeechBubble>();
    }

    /**
     * The Box2d world setter.
     * This is meant to be called by the level class whenever a new level is loaded.
     * @param newWorld The new box2d world.
     */
    public void setWorld(World newWorld)
    {
        world = newWorld;

        // Never forget to set the contact listener back.
        world.setContactListener(contactListener);
    }

    /**
     * The Box2d world getter.
     * @return The box2d world.
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * The world ratio getter.
     * @return The ratio of pixels to meters.
     */
    public float ratio()
    {
        return ratio;
    }

    /**
     * The display metrics getter.
     * @return The display metrics object containing screen characteristics.
     */
    public DisplayMetrics getDisplayMetrics()
    {
        return displayMetrics;
    }

    /**
     * The resources getter.
     * @return The project resources object.
     */
    public Resources getResources()
    {
        return resources;
    }

    /**
     * The method to initialize the required sensors. Called by the Main Activity.
     * @param newSensorManager The new object to replace sensorManager with
              before using it to set the sensors.
     */
    public void initializeSensors(SensorManager newSensorManager)
    {
        sensorManager = newSensorManager;

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    /**
     * The box2d fps setter.
     * @param newFps The new value for fps.
     */
    public void setFps(float newFps)
    {
        fps = newFps;
    }

    /**
     * The box2d fps getter.
     * @return The current Fps of the box2d thread.
     */
    public float getFps()
    {
        return fps;
    }

    /**
     * The gravity getter.
     * This is set by the android gravity sensor.
     * @return The gravity due to the phone orientation.
     */
    public Vec2 getGravity()
    {
        return gravity;
    }

    /**
     * The getter for the main character.
     * @return The main character.
     */
    public Blob getBlob()
    {
        return blob;
    }

    /**
     * The main character setter.
     * @param newBlob The new instance of blob.
     */
    public void setBlob(Blob newBlob)
    {
        blob = newBlob;
    }

    /**
     * The level getter.
     * @return The currentLevel.
     */
    public Level getLevel()
    {
        return level;
    }

    /**
     * The setter for the level.
     * @param level The new level.
     */
    public void setLevel(Level level)
    {
        this.level = level;
    }

    /**
     * The getter for all the actors.
     * @return HashSet with all actors.
     */
    public HashSet<Actor> getActors()
    {
        return actors;
    }

    /**
     * Queue the actor to have its physical entities created
     * as soon as it is safe to do so.
     * This is meant to be called by the Actor's constructor.
     * @param actor The actor to be queued.
     */
    public void queueActor(Actor actor)
    {
        toBeCreated.add(actor);
    }

    /**
     * Safely create an actor's physical entities.
     */
    public void createAllEntities()
    {
        for (Actor actor : toBeCreated)
        {
            actor.createEntity();
        }

        //Clear the set now that they were all added.
        toBeCreated.clear();
    }


    /**
     * Adds a complete actor to the array list of all actors.
     * This is meant to be called by the Actor's createEntity method.
     * @param actor The actor to be added.
     */
    public void pushActor(Actor actor)
    {
        if (actor != null)
        {
            actors.add(actor);
        }
        else
        {
            Log.v("GameWorld", "A null actor was trying to be added!");
        }
    }

    /**
     * Deletes all bodies that are queued to be deleted in the toBeDeleted set.
     * This is meant to be called safely outside of the world step.     *
     */
    public void deleteBodies()
    {
        for (Body body : this.getToBeDeleted())
        {
            // TODO: Do I need to delete anything else related to these bodies?
            this.getWorld().destroyBody(body);
        }
    }

    /**
     * Returns the hash set of bodies that will be deleted as soon as it is safe
     * to do so.
     * @return Bodies to be deleted.
     */
    public HashSet<Body> getToBeDeleted()
    {
        return toBeDeleted;
    }

    /**
     * Call the specific logic for every actor and doodle.
     */
    public void runAI()
    {
        for (Actor actor : getActors())
        {
            actor.charLogic();
        }

        for (Doodle doodle : getLevel().getDoodles())
        {
            doodle.runLogic();
        }
    }

    /**
     * Draws all the actors in the actors set.
     * @param canvas The canvas to draw on.
     */
    public void drawActors(Canvas canvas)
    {
        for (Actor actor : this.getActors())
        {
            if (actor != null)
            {
                actor.drawMe(canvas);
            }
        }
    }

    /**
     * Draws any all speech bubbles in the current set.
     * @param canvas The canvas to draw on.
     */
    public void drawSpeechBubbles(Canvas canvas)
    {
        for (SpeechBubble bubble : this.getSpeechBubbles())
        {
            bubble.drawMe(canvas);
        }
    }

    /**
     * The main activity calls this whenever it gets resumed.
     */
    public void onResume()
    {
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * The main activity calls this whenever it is paused.
     */
    public void onPause()
    {
        sensorManager.unregisterListener(this);
    }

    /**
     * This gets called whenever a sensor's accuracy changes.
     * I have no use for this right now.
     * @param changedSensor The sensor that changed.
     * @param accuracy the new accuracy.
     */
    public void onAccuracyChanged(Sensor changedSensor, int accuracy)
    {
        //Empty implemented method.
    }

    /**
     * This gets called whenever a sensor changes (it detects something different).
     */
    public void onSensorChanged(SensorEvent event)
    {
        gravity = new Vec2(event.values[0], event.values[1]);
    }

    /**
     * Returns the set of all speech bubbles being currently displayed.
     * @return The hash set of all speech bubbles to draw.
     */
    public HashSet<SpeechBubble> getSpeechBubbles()
    {
        return speechBubbles;
    }
}
