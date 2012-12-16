package com.daedalusgames.shotgun_blob;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *  This class will be used by box2d for all callbacks of contacts between
 *  fixtures/bodies.
 *  It receives the input and decides what classes should be notified of the
 *  event.
 *
 *  @author Artur
 *  @version Dec 15, 2012
 */
public class ContactListener implements org.jbox2d.callbacks.ContactListener
{
    private GameWorld gameWorld;

    /**
     * The constructor. Initializes its only field.
     * @param gameWorld The game world reference.
     */
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
        /*
        else if ( !fixtureA.isSensor() && !fixtureB.isSensor() )//if neither of the parties is a sensor
        {
            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();

            //generalCollision( bodyA, bodyB );
        }*/
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

    /**
     * Called when one of the colliding fixtures is a sensor.
     * @param sensorFixture The fixture identified as a sensor.
     * @param otherFixture The other fixture.
     * @param beginContact True if the contact is beginning, false if it is ending.
     */
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


        for (Action action : gameWorld.getLevel().getActions())
        {

            if (action.getSensorTrigger() == sensorFixture)
            {
                action.sensorActivated(otherFixture, beginContact);
                return; // If it was already found, I'm done.
            }
        }
    }

}
