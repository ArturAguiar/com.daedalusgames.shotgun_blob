package com.daedalusgames.shotgun_blob;

import com.daedalusgames.shotgun_blob.Actor.ActorType;
import org.jbox2d.dynamics.Fixture;

/**
 *  Actions are executed by actors one at a time.
 *  Such actions are level-specific at this point and can be triggered by a
 *  sensor or by a condition specified to the constructor.
 *
 *  Use an anonymous inner class object of type Action.ConditionalTrigger to
 *  specify a method that returns a boolean which indicates whether the action
 *  should be triggered or not.
 *
 *  @author Artur
 *  @version Dec 15, 2012
 */
public class Action
{
    /**
     *  The type of this action.
     *  Used so that the actor knows which actions to carry out.
     */
    public enum ActionType
    {
        /** The actor stops moving and waits for the timer to run out. */
        WAIT,

        /** The actor moves at the speed provided for the time provided. */
        MOVE,

        /** The actor talks the text provided. */
        TALK,

        /** No action is taken during the time provided. */
        DELAY
    };
    private ActionType type = ActionType.DELAY;

    private GameWorld gameWorld;

    private Actor subject;

    private ConditionalTrigger conditionTrigger;

    private Fixture sensorTrigger;

    private int targetContacts;

    private int totalFrames;
    private int currentFrame;

    private boolean restrains;

    private Action head;

    private Action next;

    private SpeechBubble bubble;

    private float moveSpeed;

    private int runsCounter = 0;

    private boolean repeats = false;

    private boolean running = false;

    private boolean finished = false;

    /**
     * This constructor takes the minimum input necessary to create an action.
     * @param subject The subject that will be taking this action.
     * @param gameWorld The game world reference.
     * @param repeats Whether this action should run again if the trigger is
     *        reactivated or only run once.
     */
    public Action(Actor subject, GameWorld gameWorld, boolean repeats)
    {
        this.head = this;

        this.subject = subject;

        this.gameWorld = gameWorld;

        this.repeats = repeats;
    }

    private Action(Actor subject, GameWorld gameWorld, boolean repeats, Action head)
    {
        this.head = head;

        this.subject = subject;

        this.gameWorld = gameWorld;

        this.repeats = repeats;
    }

    /**
     * Adds a conditional trigger to this action.
     * Use an anonymous inner class object of type Action.ConditionalTrigger to
     * specify a method that returns a boolean which indicates whether the action
     * should be triggered or not.
     * @param conditionalTrigger The trigger to determine if this action should be activated.
     * @return This action (allows chaining).
     */
    public Action addTrigger(ConditionalTrigger conditionalTrigger)
    {
        this.conditionTrigger = conditionalTrigger;

        return this;
    }

    /**
     * Adds a sensor trigger to this action.
     * When the sensor collides with an actor of the specified type, the action
     * will be activated.
     * @param mySensorTrigger The sensor that activates this action.
     * @return This action (allows chaining).
     */
    public Action addTrigger(Fixture mySensorTrigger)
    {
        this.sensorTrigger = mySensorTrigger;

        return this;
    }

    /**
     * Checks if the condition given by the conditional trigger returns true,
     * if so this action is activated.
     * This is meant to be called at every frame.
     */
    public void checkCondition()
    {
        if ((runsCounter == 0 || repeats) && !running && conditionTrigger != null && conditionTrigger.evaluateCondition())
        {
            getSubject().performAction(this);
            running = true;
        }
    }

    /**
     * Called when the sensor collides with another fixture.
     * Activates this action if necessary.
     * This is meant to be called by the contact listener.
     * @param otherFixture The fixture that the sensor collided with.
     * @param beginContact True if this was called by contact being initialized,
     *        false if it was by contact finishing.
     */
    public void sensorActivated(Fixture otherFixture, boolean beginContact)
    {
        if (sensorTrigger == null || otherFixture.getUserData() == null)
            return;

        Actor actor = (Actor)otherFixture.getUserData();

        // If it was blob, trigger the event.
        if (actor.getType().equals(ActorType.BLOB))
        {
            if (beginContact)
            {
                targetContacts++;
            }
            else
            {
                targetContacts--;
            }
        }

        if (targetContacts > 0 && !running)
        {
            getSubject().performAction(this);
            running = true;

            if (!repeats)
            {
                // Remove the sensor, since it isn't needed anymore.
                gameWorld.getToBeDeleted().add(sensorTrigger.getBody());
            }
        }

    }

    /**
     * Adds a move action to the action chain.
     * @param myMoveSpeed The speed to apply during the given time.
     * @param amountFrames The time in frames that the action should go on for.
     * @return A new action chained to this one so that the chain can continue.
     */
    public Action move(float myMoveSpeed, int amountFrames)
    {
        this.restrains = true;
        this.type = ActionType.MOVE;
        this.moveSpeed = myMoveSpeed;

        this.totalFrames = amountFrames;
        this.currentFrame = this.totalFrames;

        return this.chainTo(new Action(subject, gameWorld, repeats, head));
    }

    /**
     * Adds a talk action to the action chain.
     * @param speech The text that the subject should speak.
     * @param amountFrames The time in frames that the action should go on for.
     * @return A new action chained to this one so that the chain can continue.
     */
    public Action talk(String speech, int amountFrames)
    {
        this.type = ActionType.TALK;
        this.totalFrames = amountFrames;
        this.currentFrame = this.totalFrames;

        this.bubble = new SpeechBubble(gameWorld, subject, speech);

        return this.chainTo(new Action(subject, gameWorld, repeats, head));
    }

    /**
     * Adds a wait action to the action chain.
     * @param amountFrames The time in frames that the action should go on for.
     * @return A new action chained to this one so that the chain can continue.
     */
    public Action waitFor(int amountFrames)
    {
        this.restrains = true;
        this.type = ActionType.WAIT;
        this.totalFrames = amountFrames;
        this.currentFrame = this.totalFrames;

        return this.chainTo(new Action(subject, gameWorld, repeats, head));
    }

    /**
     * Chains an action to another.
     * @param nextAction The action to be triggered after the calling action
     *        finishes.
     * @return The next action (the one passed) to allow chaining.
     */
    public Action chainTo(Action nextAction)
    {
        this.next = nextAction;

        return this.next;
    }

    /**
     * Returns the next action in the chain.
     * Resets all variables to the default values before that.
     * If this is the last action in the chain, it informs the first action
     * (head) that the action chain finished execution.
     *
     * @return The next action on the chain.
     */
    public Action next()
    {
        this.currentFrame = totalFrames;
        this.finished = false;

        if (next == null)
        {
            head.running = false;
        }

        return next;
    }

    /**
     * Makes this action restrain the actor while it is running.
     * @return This action (allows chaining).
     */
    public Action restrain()
    {
        this.restrains = true;

        return this;
    }

    /**
     * Returns the subject of this action.
     * @return The actor that should carry out this action.
     */
    public Actor getSubject()
    {
        return subject;
    }

    /**
     * Returns the sensor that should trigger this action, if any.
     * @return The sensor fixture, or null if none was added.
     */
    public Fixture getSensorTrigger()
    {
        return sensorTrigger;
    }

    /**
     * Decrements one action frame.
     * This is meant to be called by the actor when it has already handled the
     * action on a frame.
     */
    public void decrementFrame()
    {
        this.currentFrame -= 1;

        if (this.currentFrame <= 0)
        {
            this.finished = true;
            runsCounter++;
        }
    }

    /**
     * Determines if this action finished or not.
     * @return True if this action finished, false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Determines if this action restrains the subject or not.
     * @return True if this action restrains the subject, false otherwise.
     */
    public boolean restrains()
    {
        return restrains;
    }

    /**
     * Determines which type of action this is.
     * @return The type of this action.
     */
    public ActionType getType()
    {
        return type;
    }

    /**
     * Returns the speech bubble of this action.
     * It is only possible to have a speech bubble if this action is of the
     * type 'TALK'.
     * @return The speech bubble of this action, null if none was provided.
     */
    public SpeechBubble getSpeechBubble()
    {
        return bubble;
    }

    /**
     * Returns the move speed of this action.
     * It is only possible to have a move speed if this action is of the
     * type 'MOVE'.
     * @return The move speed of this action.
     */
    public float getMoveSpeed()
    {
        return moveSpeed;
    }

    /**
     * Determines if this action repeats if triggered again, or if it only
     * runs once.
     * @return False if this action only runs once, true otherwise.
     */
    public boolean repeats()
    {
        return repeats;
    }

    /**
     *  This is meant to be used as an anonymous inner class object of type Action.ConditionalTrigger to
     *  specify a method that returns a boolean which indicates whether the action
     *  should be triggered or not.
     *
     *  @author Artur
     *  @version Dec 15, 2012
     */
    public abstract static class ConditionalTrigger
    {
        /**
         * This method should be defined to return a boolean that indicates
         * whether the action should be triggered or not.
         * @return True if this action should be triggered, false otherwise.
         */
        public abstract boolean evaluateCondition();

    }
}
