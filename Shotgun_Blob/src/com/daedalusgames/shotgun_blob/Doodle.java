package com.daedalusgames.shotgun_blob;

import android.graphics.Canvas;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.Body;

/**
 *  Doodles are common elements in a level that interact with the character
 *  in some way. Like: doors and spikes.
 *
 *  @author Artur
 *  @version Nov 23, 2012
 */
public abstract class Doodle
{
    /** The box2d body. */
    private Body body;

    /** A sensor fixture for most doodles. */
    private Fixture sensor;

    /**
     * Method called when this doodle's sensor collides with another fixture.
     * @param otherFixture The fixture collided with.
     * @param beginContact True if the contact is beginning or false if ending.
     */
    abstract public void collidedWith(Fixture otherFixture, boolean beginContact);

    /**
     * Method called at every frame.
     */
    abstract public void runLogic();

    /**
     * This method draws this doodle on the canvas provided.
     * @param canvas The canvas to draw on.
     */
    abstract public void drawMe(Canvas canvas);

    /**
     * Sets the body of the doodle.
     * Should only be called by inheriting classes.
     * @param newBody The new box2d body to be set.
     */
    protected void setBody(Body newBody)
    {
        body = newBody;
    }

    /**
     * Returns the box2d body of the doodle.
     * @return body The box2d body of the doodle.
     */
    public Body getBody()
    {
        return body;
    }

    /**
     * Returns this doodle's sensor.
     * Not all doodles have sensors, it may be null.
     * @return This doodle's sensor.
     */
    public Fixture getSensor()
    {
        return sensor;
    }

    /**
     * The sensor's setter.
     * @param sensor The fixture of this doodle's sensor.
     */
    protected void setSensor(Fixture sensor)
    {
        this.sensor = sensor;
    }
}
