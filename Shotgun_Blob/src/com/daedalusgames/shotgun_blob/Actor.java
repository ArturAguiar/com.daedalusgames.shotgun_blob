package com.daedalusgames.shotgun_blob;

import android.graphics.Canvas;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Body;

/**
 * // -------------------------------------------------------------------------
/**
 *  An abstract class of all actors in the game.
 *  This is basically the superclass of every dynamic object or character.
 *
 *  @author Artur
 *  @version Sep 24, 2012
 */
abstract public class Actor
{
    /** The box2d body */
    private Body body;

    /** The box2d bodyDef */
    private BodyDef bodyDef;


    /**
     * Method responsible for drawing the sprite on the screen.
     * @param canvas The canvas to draw on.
     */
    abstract public void drawMe(Canvas canvas);

    /**
     * Sets the body of the actor.
     * Should only be called by inheriting classes.
     * @param newBody The new box2d body to be set.
     */
    protected void setBody(Body newBody)
    {
        body = newBody;
    }

    /**
     * Returns the box2d body of the actor.
     * @return body The box2d body of the actor.
     */
    public Body getBody()
    {
        return body;
    }

    /**
     * Sets the body definition of the actor.
     * Should only be called by inheriting classes.
     * @param newBodyDef The new box2d bodyDef to be set.
     */
    protected void setBodyDef(BodyDef newBodyDef)
    {
        bodyDef = newBodyDef;
    }

    /**
     * Returns the box2d body definition of the actor.
     * @return bodyDef The box2d body definition of the actor.
     */
    public BodyDef getBodyDef()
    {
        return bodyDef;
    }

    /**
     * Returns the dp (density-independent pixel) equivalent to amount of
     * pixels given as parameter.
     * @param px The amount of pixels to convert.
     * @return The equivalent amount of dp's.
     */
    public float toDP(float px)
    {
        return (px / (Main.displayMetrics.densityDpi / 160.0f));
    }
}
