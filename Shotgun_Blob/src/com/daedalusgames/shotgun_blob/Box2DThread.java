package com.daedalusgames.shotgun_blob;

import android.util.Log;


/**
 * // -------------------------------------------------------------------------
/**
 *  This thread will handle all of the logic in Box2D (The physics).
 *
 *  @author Artur
 *  @version Sep 30, 2012
 */
public class Box2DThread extends Thread
{
    private boolean run;
    private float timeStep;
    private int velocityIterations;
    private int positionIterations;

    /**
     * Thread constructor.
     */
    public Box2DThread()
    {
        run = false;
        timeStep = 1.0f / 40.0f;
        velocityIterations = 4;
        positionIterations = 2;
    }

    /**
     * Change the value of run.
     * Make it false to stop the physics.
     * @param newRun The new value for run.
     */
    public void setRunning(boolean newRun)
    {
        run = newRun;
    }

    @Override
    public void run()
    {
        while(run)
        {
            long startTime = System.nanoTime();

            if (Main.getWorld() != null)
            {
                //Main.getWorld().setGravity(Main.getGravity());
                /*if (Main.getBlob() != null && Main.getBlob().getBody() != null)
                {
                    Main.getBlob().getBody().setLinearVelocity(new Vec2(Main.getGravity().y, 0.0f));
                }*/

                Main.getWorld().step(timeStep,
                                     velocityIterations,
                                     positionIterations);

                //Call the character specific logic for every actor.
                for (Actor actor : Main.getActors())
                {
                    actor.charLogic();
                }
            }

            try
            {
                int sleepTimeNanos = (int)(timeStep * 1000000000) - (int)(System.nanoTime() - startTime);

                if (sleepTimeNanos > 0)
                {
                    int millis = sleepTimeNanos / 1000000;
                    //Log.i("Box2D Thread", "slept " + millis + "ms");
                    Thread.sleep( millis, sleepTimeNanos - millis*1000000 );
                }
                else
                {
                    Log.i("Box2D Thread", "running behind!");
                }

                //Thread.sleep(Math.round(timeStep * 1000));
                // TODO: How reliable is this method of capping the frame rate?
            }
            catch (InterruptedException e)
            {
                Log.v("Box2D Thread Exception", e.getMessage());
            }
        }
    }
}
