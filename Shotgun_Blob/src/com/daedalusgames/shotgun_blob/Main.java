package com.daedalusgames.shotgun_blob;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import org.jbox2d.common.Vec2;

/**
 * // -------------------------------------------------------------------------
/**
 *  The main activity for the game. Also the launcher.
 *
 *  @author Artur
 *  @version Sep 25, 2012
 */
public class Main extends Activity
{
    //Fields-----------------------------------------------------------------

    /** The whole game world */
    private GameWorld gameWorld;



    //Methods-----------------------------------------------------------------
    /**
     * This method gets executed when the activity is created.
     * It functions as a constructor.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        gameWorld = new GameWorld(new Vec2(0.0f, 10.0f), true);

        getWindowManager().getDefaultDisplay().getMetrics(gameWorld.getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(new Panel(this, gameWorld));

        gameWorld.initializeSensors((SensorManager) getSystemService(SENSOR_SERVICE));
    }

    /**
     * This method gets called when the application looses focus.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        gameWorld.onPause();
    }

    /**
     * This method gets called when the application regains focus.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        gameWorld.onResume();
    }

}
