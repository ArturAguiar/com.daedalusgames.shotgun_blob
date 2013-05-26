package com.daedalusgames.shotgun_blob;

import java.util.HashMap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.util.HashSet;
import org.jbox2d.common.Vec2;
import java.io.IOException;
import java.io.InputStream;
import org.jbox2d.dynamics.World;
import android.util.Log;
import org.iforce2d.Jb2dJson;
import org.jbox2d.dynamics.Fixture;
import android.graphics.RectF;
import org.jbox2d.dynamics.Body;

/**
 * // -------------------------------------------------------------------------
/**
 *  Temporary level class to build the boundary edges.
 *
 *  @author Artur
 *  @version Oct 1, 2012
 */
public class Level
{
    private GameWorld gameWorld;

    private String levelName;

    private RectF boundingBox;

    private HashSet<Doodle> levelDoodles;

    private HashSet<Action> levelActions;

    private HashMap<String, Actor> levelActors;

    private Bitmap levelImage;

    private Paint bitmapPaint;

    /**
     * Level constructor.
     * @param myGameWorld The game world.
     */
    public Level(GameWorld myGameWorld)
    {
        gameWorld = myGameWorld;

        levelDoodles = new HashSet<Doodle>();

        levelActions = new HashSet<Action>();

        levelActors = new HashMap<String, Actor>();

        //Load first level.
        this.loadLevel("intro");

        levelImage = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.intro_level);
        levelImage = Bitmap.createScaledBitmap(levelImage,
            (int)(this.boundingBox.width() * gameWorld.ratio()),
            (int)(this.boundingBox.height() * gameWorld.ratio()),
            false);

        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    /**
     * Loads a given level by resetting the box2d world.
     * Detects named doodles and acts accordingly.
     * @param myLevelName The name of the new level.
     * @return True if successful or false otherwise.
     */
    public boolean loadLevel(String myLevelName)
    {
        this.levelName = myLevelName;

        Jb2dJson json = new Jb2dJson();
        StringBuilder errorMsg = new StringBuilder();
        World newWorld = json.readFromString(assetToString(levelName + ".json"), errorMsg);

        if (!errorMsg.toString().equals(""))
        {
            Log.v("GameWorld", errorMsg.toString());
            return false;
        }

        // Clear the actors and doodles and set the brand neeew woooooorld.
        gameWorld.getActors().clear();
        levelDoodles.clear();
        gameWorld.setWorld(newWorld);

        // Add blob to the starting point
        Body startPoint = json.getBodyByName("startPoint");

        if (startPoint != null)
        {
            Vec2 startPosition = startPoint.getPosition();

            if (gameWorld.getBlob() != null)
            {
                // Recreate the physical body of blob at the new position,
                // since the the last one was destroyed with the old world.
                gameWorld.getBlob().setInitialPosition(startPosition);
                gameWorld.queueActor(gameWorld.getBlob());
            }
            else
            {
                // Create a new blob instance if none is there.
                gameWorld.setBlob(new Blob(gameWorld, startPosition.x * gameWorld.ratio(), startPosition.y * gameWorld.ratio()));
            }
            gameWorld.getWorld().destroyBody(startPoint);
        }
        else
        {
            // TODO: I want to throw an exception here, but I'm not sure this is the right one.
            throw new IllegalStateException("A starting point was not set in the level.");
        }


        // Check for level actors.
        Body[] levelActorBodies = json.getBodiesByName("actor");
        for (int i = 0; i < levelActorBodies.length; i++)
        {
            String name = json.getFixtureName(levelActorBodies[i].getFixtureList());
            levelActors.put(name.trim(), new LevelObject(this.gameWorld, levelActorBodies[i], null));
        }

        // Check for doors.
        Body[] doors = json.getBodiesByName("doodle_door");
        for (int i = 0; i < doors.length; i++)
        {
            // Get the respective sensor.
            int id = Integer.parseInt(json.getFixtureName(doors[i].getFixtureList()));
            Fixture sensor = json.getFixtureByName("doodle_door_sensor_" + id);

            // Add the door to the doodles set for the level.
            levelDoodles.add(new Door(doors[i], sensor, gameWorld));
        }
        Log.v("Level", "Found " + doors.length + " doors.");


        // Check for event sensors.
        Body[] eventSensors = json.getBodiesByName("doodle_event_sensor");
        for (int i = 0; i < eventSensors.length; i++)
        {
            Fixture sensor = eventSensors[i].getFixtureList();
            int id = Integer.parseInt(json.getFixtureName(sensor));

            this.addAction(id, sensor);
        }


        // Calculate the bounding box.
        boundingBox = this.calculateBoundingBox();

        return true;
    }

    private void addAction(int id, Fixture sensor)
    {
        Action actionHead = null;
        boolean eventNotFound = false;

        if (this.levelName.equals("intro"))
        {
            switch(id)
            {
                case 0:
                    actionHead = new Action(gameWorld.getBlob(), gameWorld, false);

                    actionHead.waitFor(30)
                    .restrain().talk("The fuck is going on?!", 100);
                    //.move(-gameWorld.getBlob().getMaxSpeed(), 80)
                    //.waitFor(30)
                    //.restrain().talk("Wherever it is that I came from,", 100)
                    //.talk("I can't go back.", 70);

                    actionHead.addTrigger(sensor);

                    /* This is an example of a conditional trigger.

                    actionHead.addTrigger(
                        new Action.ConditionalTrigger() {
                            @Override
                            public boolean evaluateCondition()
                            {
                                if (gameWorld.getBlob().getReloadTimer() > 0)
                                    return true;

                                return false;
                            }
                        }
                    );
                    */

                    break;

                case 1:
                    Actor speaker = levelActors.get("speaker1");
                    if (speaker == null)
                        Log.v("Level", "Couldn't find speaker!");
                    else
                        Log.v("Level", "found speaker at " + speaker.getBody().getPosition());

                    actionHead = new Action(speaker, this.gameWorld, false);

                    actionHead.talk("Test subject b10b, please proceed to the room on your right", 100);
                    actionHead.addTrigger(sensor);
                    break;

                default:
                    eventNotFound = true;
                    break;
            }
        }

        if (!eventNotFound)
        {
            levelActions.add(actionHead);
        }
        else
        {
            // TODO: I want to throw an exception here, but I'm not sure this is the right one.
            throw new IllegalStateException("The event of id " + id + " was not found in the level " + this.levelName + ".");
        }

    }

    /**
     * Calculates the bounding box based on the fixtures.
     */
    private RectF calculateBoundingBox()
    {
        RectF box = null;

        //Iterate through the list of bodies in the world to get their fixtures.
        for (Body body = gameWorld.getWorld().getBodyList(); body != null; body = body.getNext())
        {
            //Now iterate through every fixture inside the body.
            for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext())
            {
                // Do not consider sensors.
                if (!fixture.isSensor())
                {
                    float left = fixture.getAABB().lowerBound.x;
                    float right = fixture.getAABB().upperBound.x;
                    float bottom = fixture.getAABB().upperBound.y;
                    float top = fixture.getAABB().lowerBound.y;

                    if ( box == null )
                    {
                        box = new RectF(left, top, right, bottom);
                    }
                    else
                    {
                        if ( left < box.left )
                            box.left = left;

                        if ( right > box.right )
                            box.right = right;


                        if ( top < box.top )
                            box.top = top;

                        if ( bottom > box.bottom )
                            box.bottom = bottom;
                    }
                }
            }
        }

        Log.v("Level", "Left-top = (" + box.left + ", " + box.top + ")");
        Log.v("Level", "Right-bottom = (" + box.right + ", " + box.bottom + ")");

        return box;
    }

    /**
     * Returns this level's bounding box.
     * @return The bounding box of this level.
     */
    public RectF getBoundingBox()
    {
        return boundingBox;
    }

    /**
     * Returns the hash set of doodles.
     * @return This level's doodles.
     */
    public HashSet<Doodle> getDoodles()
    {
        return levelDoodles;
    }

    /**
     * Returns the hash set of events.
     * @return This levels events.
     */
    /*
    public HashSet<Event> getEvents()
    {
        return levelEvents;
    }
    */

    public HashSet<Action> getActions()
    {
        return levelActions;
    }


   /**
    * Returns a string representation of the given asset.
    * Meant to be used to decode json levels.
    * @param filename The name of the asset file.
    * @return The asset as a string.
    */
   public String assetToString(String filename)
   {
       try {
           InputStream is = gameWorld.getResources().getAssets().open(filename);

           // We guarantee that the available method returns the total
           // size of the asset...  of course, this does mean that a single
           // asset can't be more than 2 gigs.
           int size = is.available();

           // Read the entire asset into a local byte buffer.
           byte[] buffer = new byte[size];
           is.read(buffer);
           is.close();

           // Convert the buffer into a string.
          return new String(buffer);
       }
       catch (IOException e)
       {
           // Should never happen!
           throw new RuntimeException(e);
       }
   }

   /**
    * Draws the level on the provided canvas.
    * The image of the level is positioned on its top-left corner.
    * @param canvas The canvas to draw on.
    */
   public void drawMe(Canvas canvas)
   {
       for (Doodle doodle : this.getDoodles())
       {
           doodle.drawMe(canvas);
       }

       canvas.drawBitmap(levelImage,
           this.boundingBox.left * gameWorld.ratio(),
           this.boundingBox.top * gameWorld.ratio(),
           bitmapPaint);
   }

}
