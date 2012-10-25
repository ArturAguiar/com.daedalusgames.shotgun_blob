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
    private GameWorld gameWorld;

    private boolean run;
    private float timeStep;
    private int velocityIterations;
    private int positionIterations;

    /**
     * Thread constructor.
     * @param myGameWorld The reference to the game world;
     */
    public Box2DThread(GameWorld myGameWorld)
    {
        gameWorld = myGameWorld;

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

            if (gameWorld.getWorld() != null)
            {
                gameWorld.getWorld().step(timeStep,
                                     velocityIterations,
                                     positionIterations);

                //Call the character specific logic for every actor.
                for (Actor actor : gameWorld.getActors())
                {
                    actor.charLogic();
                }
            }

            try
            {
                long currentTime = System.nanoTime();
                int sleepTimeNanos = (int)(timeStep * 1000000000) - (int)(currentTime - startTime);

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
                long endTime = System.nanoTime();

                gameWorld.setFps(1000000000.0f / (endTime - startTime));
            }
            catch (InterruptedException e)
            {
                Log.v("Box2D Thread Exception", e.getMessage());
            }
        }
    }
}
