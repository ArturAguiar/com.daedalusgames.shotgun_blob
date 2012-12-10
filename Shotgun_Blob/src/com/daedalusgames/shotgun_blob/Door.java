package com.daedalusgames.shotgun_blob;

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
    private boolean open;

    private int closeTimer;

    private float closedPosition;
    private float openPosition;

    private float speed;

    /** This is a counter for me to be able to tell if the contact is still going on or not. */
    private int blobContacts;

    /** An ID that I can refer to. Unique per level. */
    private int id;

    /**
     * Constructor for a level door.
     * Meant to be called by the level loader.
     * @param doorBody The body of this door.
     * @param doorSensor The sensor of this door.
     * @param id The id of this door (unique per level).
     */
    public Door(Body doorBody, Fixture doorSensor, int id)
    {
        this.setBody(doorBody);

        this.setSensor(doorSensor);

        this.id = id;

        open = false;

        closeTimer = 0;

        closedPosition = this.getBody().getPosition().y;

        AABB boundingBox = this.getBody().getFixtureList().getAABB();
        openPosition = boundingBox.upperBound.y - boundingBox.lowerBound.y + closedPosition;

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
        if (actor.getType().equals(Actor.Type.BLOB))
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
            if (this.getBody().getPosition().y < openPosition)
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, speed));
            }
            else
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
            }
        }
        else
        {
            if (this.getBody().getPosition().y > closedPosition)
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, -speed));
            }
            else
            {
                this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
            }
        }
    }

    /**
     * The unique level id getter.
     * @return The id of this door.
     */
    public int getId()
    {
        return id;
    }
}
