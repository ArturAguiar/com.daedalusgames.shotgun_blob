package com.daedalusgames.shotgun_blob;

import org.jbox2d.common.Vec2;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * // -------------------------------------------------------------------------
/**
 *  Custom android sensor class.
 *
 *  @author Artur
 *  @version Oct 5, 2012
 */
public class DeviceSensor implements SensorEventListener
{
    private final Sensor sensor;

    /**
     * The sensor constructor.
     * @param sensorType The type of the sensor (use enum type in android.Sensor).
     */
    public DeviceSensor(int sensorType)
    {
        sensor = Main.getSensorManager().getDefaultSensor(sensorType);
    }

    /**
     * The main activity calls this whenever it gets resumed.
     */
    public void onResume()
    {
        Main.getSensorManager().registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * The main activity calls this whenever it is paused.
     */
    public void onPause()
    {
        Main.getSensorManager().unregisterListener(this);
    }

    /**
     * This gets called whenever the sensor's accuracy changes.
     * I have no use for this right now.
     * @param changedSensor The sensor that changed.
     * @param accuracy the new accuracy.
     */
    public void onAccuracyChanged(Sensor changedSensor, int accuracy)
    {
        //Empty implemented method.
    }

    /**
     * This gets called whenever the sensor changes (it detects something different).
     */
    public void onSensorChanged(SensorEvent event)
    {
        Main.setGravity( new Vec2(-1.0f*event.values[0], event.values[1]) );
        //float values[] = event.values;
    }

}