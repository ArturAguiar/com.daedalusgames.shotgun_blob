package com.daedalusgames.shotgun_blob;

import android.util.Log;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class ContactListener implements org.jbox2d.callbacks.ContactListener
{
    private GameWorld gameWorld;

    public ContactListener(GameWorld gameWorld)
    {
        this.gameWorld = gameWorld;
    }


    // IMPLEMENTED METHODS.

    public void beginContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if ( fixtureA.isSensor() && !fixtureB.isSensor() )
        {
            sensorCollision( fixtureA, fixtureB, true );
        }
        else if ( !fixtureA.isSensor() && fixtureB.isSensor() )
        {
            sensorCollision( fixtureB, fixtureA, true );
        }
        else if ( !fixtureA.isSensor() && !fixtureB.isSensor() )//if neither of the parties is a sensor
        {
            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();

            //generalCollision( bodyA, bodyB );
        }
    }

    public void endContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if ( fixtureA.isSensor() && !fixtureB.isSensor() )
        {
            sensorCollision( fixtureA, fixtureB, false );
        }
        else if ( !fixtureA.isSensor() && fixtureB.isSensor() )
        {
            sensorCollision( fixtureB, fixtureA, false );
        }
    }

    public void postSolve(Contact arg0, ContactImpulse arg1)
    {
        // TODO Auto-generated method stub

    }

    public void preSolve(Contact arg0, Manifold arg1)
    {
        // TODO Auto-generated method stub

    }


    // OTHER METHODS.

    public void sensorCollision(Fixture sensorFixture, Fixture otherFixture, boolean beginContact)
    {
        for (Doodle doodle : gameWorld.getLevel().getDoodles())
        {
            if (doodle.getSensor() == sensorFixture)
            {
                doodle.collidedWith(otherFixture, beginContact);
                return; // If it was already found, I'm done.
            }
        }


        for (Event event : gameWorld.getLevel().getEvents())
        {
            Log.v("ContactListener", "Checking for event sensors.");

            if (event.getSensor() == sensorFixture)
            {
                event.sensorActivated(otherFixture, beginContact);
                Log.v("ContactListener", "Event triggered.");
                return; // If it was already found, I'm done.
            }
        }
    }

}
