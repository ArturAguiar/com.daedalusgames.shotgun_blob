package com.daedalusgames.shotgun_blob;

import android.util.Log;
import android.graphics.RectF;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import com.daedalusgames.shotgun_blob.Actor.ActorType;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.Body;

/**
 *  A door doodle. This is meant to be automatically created when the level
 *  loads through the json file.
 *  For a door to be correctly created, the json file has to contain a body
 *  with the name 'doodle_door' with a fixture named with an id (integer).
 *  It is also required for the level to have a sensor with a fixture named
 *  'doodle_door_(id)' where (id) is the integer id mentioned previously.
 *
 *  @author Artur
 *  @version Nov 24, 2012
 */
public class Door extends Doodle
{
    private GameWorld gameWorld;

    private boolean open;

    private int closeTimer;

    private float closedPosition;
    private float openPosition;

    private float speed;

    /** This is a counter for me to be able to tell if the contact is still going on or not. */
    private int blobContacts;

    /**
     * Constructor for a level door.
     * Meant to be called by the level loader.
     * @param doorBody The body of this door.
     * @param doorSensor The sensor of this door.
     * @param gameWorld The game world reference.
     */
    public Door(Body doorBody, Fixture doorSensor, GameWorld gameWorld)
    {
        this.gameWorld = gameWorld;

        this.setBody(doorBody);

        this.setSensor(doorSensor);

        open = false;

        closeTimer = 0;

        closedPosition = this.getBody().getPosition().y;

        AABB boundingBox = this.getBody().getFixtureList().getAABB();
        openPosition = closedPosition - (boundingBox.upperBound.y - boundingBox.lowerBound.y);

        Log.v("Door", "Closed position = " + closedPosition);
        Log.v("Door", "Open position = " + openPosition);
        Log.v("Door", "Height = " + (boundingBox.upperBound.y - boundingBox.lowerBound.y));

        speed = 5.0f;

    }

    @Override
    public void collidedWith(Fixture otherFixture, boolean beginContact)
    {
        if (otherFixture.getUserData() == null)
        {
            return;
        }

        Actor actor = (Actor)otherFixture.getUserData();

        // If it was blob, open the door.
        if (actor.getType().equals(ActorType.BLOB))
        {
            if (beginContact)
            {
                blobContacts++;
                open = true;
            }
            else
            {
                blobContacts--;
                if (blobContacts == 0)
                {
                    // Wait 60 frames before closing.
                    closeTimer = 60;
                }
            }
        }

    }

    @Override
    public void runLogic()
    {
        if (closeTimer > 0)
        {
            closeTimer--; //decrements the timer (the delay time for doors to close)
            if (closeTimer == 0)
            {
                open = false;
            }
        }

        if (blobContacts < 0) //just to be safe, but this should never happen
        {
            blobContacts = 0;
        }

        if (open)
        {
            if (this.getBody().getPosition().y > openPosition)
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, -speed));
            }
            else
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
            }
        }
        else
        {
            if (this.getBody().getPosition().y < closedPosition)
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, speed));
            }
            else
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
            }
        }
    }

    @Override
    public void drawMe(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.DKGRAY);

        AABB box = this.getBody().getFixtureList().getAABB();
        canvas.drawRect(new RectF(box.lowerBound.x * gameWorld.ratio(), box.lowerBound.y * gameWorld.ratio(),
            box.upperBound.x * gameWorld.ratio(), box.upperBound.y * gameWorld.ratio()), paint);
    }
}
