package com.daedalusgames.shotgun_blob;

import org.jbox2d.common.Vec2;
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
    /** A reference to the game world */
    protected GameWorld gameWorld;

    /** The box2d body */
    private Body body;

    /** The box2d bodyDef */
    private BodyDef bodyDef;

    /** The initial position of the actor. */
    private Vec2 initialPosition;


    /**
     * This creates the physical Box2D entities of an actor.
     */
    abstract public void createEntity();

    /**
     * Method responsible for drawing the sprite on the screen.
     * @param canvas The canvas to draw on.
     */
    abstract public void drawMe(Canvas canvas);

    /**
     * Method where all character specific logic is located.
     * This is run after every box2d step.
     */
    abstract public void charLogic();

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
     * The initial position getter.
     * @return The initial position.
     */
    protected Vec2 getInitialPosition()
    {
        return initialPosition;
    }

    /**
     * The initial position setter.
     * @param newInitialPosition The initial position of the actor's body.
     */
    protected void setInitialPosition(Vec2 newInitialPosition)
    {
        initialPosition = newInitialPosition;
    }

    /**
     * Returns the dp (density-independent pixel) equivalent to amount of
     * pixels given as parameter.
     * TODO: I don't think this is working.
     * @param px The amount of pixels to convert.
     * @return The equivalent amount of dp's.
     */
    public float toDP(float px)
    {
        return (px / (gameWorld.getDisplayMetrics().densityDpi / 160.0f));
    }
}
