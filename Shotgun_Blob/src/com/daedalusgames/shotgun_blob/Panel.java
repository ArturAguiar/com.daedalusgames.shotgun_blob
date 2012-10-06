package com.daedalusgames.shotgun_blob;

import android.view.MotionEvent;
import android.graphics.Color;
import android.graphics.Canvas;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * // -------------------------------------------------------------------------
/**
 *  The main drawing surface for the game.
 *
 *  @author Artur
 *  @version Sep 28, 2012
 */
public class Panel extends SurfaceView implements SurfaceHolder.Callback
{
    private ViewThread viewThread;

    private Box2DThread box2dThread;

    //private Bitmap myBitmap;

    private int tapX;
    private int tapY;

    /**
     * The panel constructor.
     * @param context The application context.
     */
    public Panel(Context context)
    {
        super(context);

        //myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher); // icon

        getHolder().addCallback(this);

        //Create the view (drawing) thread.
        viewThread = new ViewThread(this);

        //Create the box2D dedicated thread.
        box2dThread = new Box2DThread();
    }

    /**
     * My custom draw method.
     * Only I can call this function. Android calls onDraw() that does nothing.
     * This gives me more control over the drawing phase.
     * @param canvas The canvas to draw on.
     */
    public synchronized void doDraw(Canvas canvas)
    {
        // Color to clear the screen with.
        canvas.drawColor(Color.WHITE);

        //Draw all the actors inside the set.
        for (Actor actor : Main.getActors())
        {
            if (actor != null)
            {
                //actor.drawMe(canvas);
            }
        }

        //Debug-draw the shapes in the world
        DebugDraw.debugDrawShapes(Main.getWorld(), canvas);


        /*
        canvas.drawBitmap(myBitmap, tapX - myBitmap.getWidth() / 2, tapY - myBitmap.getHeight() / 2, null);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        canvas.drawCircle(50.0f, 50.0f, 20.0f, paint);
        */
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        // TODO: Should I handle any screen resizing here or is the game going to run in a fixed perspective?
    }


    public void surfaceCreated(SurfaceHolder holder)
    {
        // Thread can't be started more than once, so instantiate a new one
        // if it is dead.
        if (!viewThread.isAlive())
        {
            viewThread = new ViewThread(this);
            viewThread.setRunning(true);
            viewThread.start();
        }

        if (!box2dThread.isAlive())
        {
            box2dThread = new Box2DThread();
            box2dThread.setRunning(true);
            box2dThread.start();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (viewThread.isAlive())
        {
            // Make the application stop running.
            viewThread.setRunning(false);
        }

        if (box2dThread.isAlive())
        {
            // Make the application stop running.
            box2dThread.setRunning(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        tapX = (int) event.getX();
        tapY = (int) event.getY();

        @SuppressWarnings("unused")
        Blob aBlob = new Blob(tapX, tapY);

        return super.onTouchEvent(event);
    }
}
