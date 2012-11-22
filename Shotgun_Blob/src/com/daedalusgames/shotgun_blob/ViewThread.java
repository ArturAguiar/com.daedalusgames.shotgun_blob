package com.daedalusgames.shotgun_blob;

import android.util.Log;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * // -------------------------------------------------------------------------
/**
 *  My custom thread to draw/render images on the screen.
 *
 *  @author Artur
 *  @version Sep 28, 2012
 */
public class ViewThread extends Thread
{
    private Panel myPanel;
    private SurfaceHolder myHolder;
    private boolean run = false;

    /**
     * Thread constructor.
     * @param panel The panel in which drawing will be done.
     */
    public ViewThread(Panel panel) {
        myPanel = panel;
        myHolder = myPanel.getHolder();
    }

    /**
     * Change the value of run.
     * Make it false to stop drawing.
     * @param newRun The new value for run.
     */
    public void setRunning(boolean newRun) {
        run = newRun;
    }

    @Override
    public void run()
    {
        Canvas canvas = null;

        while (run)
        {
            canvas = myHolder.lockCanvas();
            if (canvas != null) {
                myPanel.doDraw(canvas);
                myHolder.unlockCanvasAndPost(canvas);
            }

            try
            {
                // 60 fps...ish
                Thread.sleep(1000/60);
            }
            catch (InterruptedException e)
            {
                Log.v("View Thread Exception", e.getMessage());
            }
        }
    }
}
