package com.daedalusgames.shotgun_blob;

import android.util.Log;
import com.daedalusgames.shotgun_blob.Actor.ActorType;
import java.util.ArrayList;
import org.jbox2d.dynamics.Fixture;

public class Event
{
    // The sensor that triggers this event if any.
    private Fixture sensor;

    // The level that this event is in.
    private GameWorld gameWorld;

    //private enum EventType { SENSOR_TRIGGERED, EVENT_TRIGGERED };
    //private EventType type;

    /** This is a counter for me to be able to tell if the contact is still going on or not. */
    private int blobContacts;

    /** An ID that I can refer to. Unique per level. */
    //private int id;

    private boolean running = false;

    private boolean runOnce;

    private ArrayList<Action> actionSequence;

    private int actionIndex = 0;

    private boolean finished = false;


    public Event(GameWorld gameWorld, Fixture sensor, ArrayList<Action> actionSequence, boolean runOnce)
    {
        this.sensor = sensor;
        this.gameWorld = gameWorld;
        this.actionSequence = actionSequence;
        this.runOnce = runOnce;

        //this.type = EventType.SENSOR_TRIGGERED;
    }


    public void sensorActivated(Fixture otherFixture, boolean beginContact)
    {
        if (sensor == null)
            return;

        if (otherFixture.getUserData() == null)
        {
            return;
        }

        Actor actor = (Actor)otherFixture.getUserData();

        // If it was blob, trigger the event.
        if (actor.getType().equals(ActorType.BLOB))
        {
            if (beginContact)
            {
                if (blobContacts == 0 && !running)
                {
                    this.executeEvent();
                    running = true;
                }

                blobContacts++;
            }
            else
            {
                blobContacts--;
            }
        }

    }

    private void executeEvent()
    {
        if (finished)
            return;

        actionSequence.get(actionIndex).getSubject().performAction(this, actionSequence.get(actionIndex));

        if (runOnce)
        {
            // Remove the sensor, since it isn't needed anymore.
            gameWorld.getToBeDeleted().add(sensor.getBody());
        }

        actionIndex++;
    }

    public void actionFinished()
    {
        if (actionIndex < actionSequence.size())
        {
            this.executeEvent();
        }
        else
        {
            actionIndex = 0;
            running = false;

            if (runOnce)
            {
                // This finishes this event. Are there any callbacks needed?
                this.finished  = true;
            }

        }
    }

    public Fixture getSensor()
    {
        return sensor;
    }
}
