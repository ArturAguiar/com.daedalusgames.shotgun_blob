package com.daedalusgames.shotgun_blob;

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

    private RectF boundingBox;

    private HashSet<Doodle> levelDoodles;

    /**
     * Level constructor.
     * @param myGameWorld The game world.
     */
    public Level(GameWorld myGameWorld)
    {
        gameWorld = myGameWorld;

        levelDoodles = new HashSet<Doodle>();

        //Load first level.
        this.loadLevel("second_test");
    }

    /**
     * Loads a given level by resetting the box2d world.
     * Detects named doodles and acts accordingly.
     * @param levelName The name of the new level.
     * @return True if successful or false otherwise.
     */
    public boolean loadLevel(String levelName)
    {
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


        // Check for doors.
        Body[] doors = json.getBodiesByName("doodle_door");
        for (int i = 0; i < doors.length; i++)
        {
            // Get the respective sensor.
            int id = Integer.parseInt(json.getFixtureName(doors[i].getFixtureList()));
            Fixture sensor = json.getFixtureByName("doodle_door_sensor_" + id);

            // Add the door to the doodles set for the level.
            levelDoodles.add(new Door(doors[i], sensor, id));
        }
        Log.v("Level", "Found " + doors.length + " doors.");


        // Calculate the bounding box.
        boundingBox = this.calculateBoundingBox();

        return true;
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
                    float top = fixture.getAABB().upperBound.y;
                    float bottom = fixture.getAABB().lowerBound.y;

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


                        if ( top > box.top )
                            box.top = top;

                        if ( bottom < box.bottom )
                            box.bottom = bottom;
                    }
                }
            }
        }

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
     * @return The doodles.
     */
    public HashSet<Doodle> getDoodles()
    {
        return levelDoodles;
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

}
