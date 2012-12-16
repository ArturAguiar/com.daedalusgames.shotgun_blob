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

    /** The facing direction */
    protected boolean facingRight;

    /** The type of an actor. */
    public enum ActorType
    {
        /** The main character. */
        BLOB,

        /** Enemies. */
        ENEMY
    };
    private ActorType type;

    private Action actionToPerform;



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
     * Tells this actor to perform the given action.
     * @param action The action to be performed.
     */
    public void performAction(Action action)
    {
        this.actionToPerform = action;
    }

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
     * This is only considered when creating the actor's entity (physical body).
     * @param newInitialPosition The initial position of the actor's body.
     */
    public void setInitialPosition(Vec2 newInitialPosition)
    {
        initialPosition = newInitialPosition;
    }

    /**
     * Returns the type of this enemy.
     * This avoids the use of instanceof to identify the type of an actor.
     * @return The type of this actor.
     */
    public ActorType getType()
    {
        return type;
    }

    /**
     * Sets the type of this actor.
     * This is only meant to be called once inside of the actor's constructor.
     * @param type The type of this actor.
     */
    protected void setType(ActorType type)
    {
        this.type = type;
    }

    /**
     * Returns the current action that the actor has to perform.
     * @return the action that this actor has to perform.
     */
    public Action getActionToPerform()
    {
        return actionToPerform;
    }

    /**
     * Changes the action being performed, if any.
     * @param actionToPerform The new action to be performed.
     */
    protected void setActionToPerform(Action actionToPerform)
    {
        this.actionToPerform = actionToPerform;
    }

    /**
     * Performs the action passed as parameter and cycles through the action
     * chain until it reaches the end (null).
     * @param action The action to execute.
     */
    protected void executeAction(Action action)
    {
        switch(action.getType())
        {
            case WAIT:

                // Try to "brake". Reduce any horizontal velocity.
                if (this.getBody().getLinearVelocity().x > 0.5f)
                {
                    this.getBody().applyLinearImpulse(new Vec2(-1.0f, 0.0f), this.getBody().getPosition());
                }
                else if (this.getBody().getLinearVelocity().x < -0.5f)
                {
                    this.getBody().applyLinearImpulse(new Vec2(1.0f, 0.0f), this.getBody().getPosition());
                }

                this.getActionToPerform().decrementFrame();

                if (this.getActionToPerform().isFinished())
                {
                    this.setActionToPerform(this.getActionToPerform().next());
                    //this.getActionEvent().actionFinished();
                }
                break;

            case MOVE:

                this.getBody().setLinearVelocity(new Vec2(this.getActionToPerform().getMoveSpeed(), this.getBody().getLinearVelocity().y));

                this.facingRight = this.getBody().getLinearVelocity().x > 0.0f;

                this.getActionToPerform().decrementFrame();

                if (this.getActionToPerform().isFinished())
                {
                    this.setActionToPerform(this.getActionToPerform().next());
                    //this.getActionEvent().actionFinished();
                }
                break;

            case TALK:

                if (!this.gameWorld.getSpeechBubbles().contains(this.getActionToPerform().getSpeechBubble()))
                {
                    this.gameWorld.getSpeechBubbles().add(this.getActionToPerform().getSpeechBubble());
                }

                this.getActionToPerform().decrementFrame();

                if (this.getActionToPerform().isFinished())
                {
                    this.gameWorld.getSpeechBubbles().remove(this.getActionToPerform().getSpeechBubble());
                    this.setActionToPerform(this.getActionToPerform().next());
                    //this.getActionEvent().actionFinished();
                }

                break;

            case DELAY:

                this.getActionToPerform().decrementFrame();

                if (this.getActionToPerform().isFinished())
                {
                    this.setActionToPerform(this.getActionToPerform().next());
                    //this.getActionEvent().actionFinished();
                }
                break;
        }



    }
}
