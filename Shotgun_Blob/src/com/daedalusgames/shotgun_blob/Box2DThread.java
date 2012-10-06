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
        timeStep = 1.0f / 60.0f;
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
            if (Main.getWorld() != null)
            {
                Main.getWorld().setGravity(Main.getGravity());

                Main.getWorld().step(timeStep,
                                     velocityIterations,
                                     positionIterations);
            }

            try
            {
                Thread.sleep(1000/60);
                // TODO: How reliable is this method of capping the frame rate?
            }
            catch (InterruptedException e)
            {
                Log.v("Box2D Thread Exception", e.getMessage());
            }
        }
    }
}
