package com.daedalusgames.shotgun_blob;

import android.util.Log;
import org.jbox2d.common.Vec2;
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
    private GameWorld gameWorld;

    private ViewThread viewThread;

    private Box2DThread box2dThread;

    private Vec2 viewportTranslation;

    /**
     * The panel constructor.
     * @param context The application context.
     * @param myGameWorld The game world.
     */
    public Panel(Context context, GameWorld myGameWorld)
    {
        super(context);
        setKeepScreenOn(true);

        gameWorld = myGameWorld;

        getHolder().addCallback(this);

        //Create the view (drawing) thread.
        viewThread = new ViewThread(this);

        //Create the box2D dedicated thread.
        box2dThread = new Box2DThread(gameWorld);

        //gameWorld.setBlob(new Blob(gameWorld));

        //gameWorld.setLevel(new Level(gameWorld));

        viewportTranslation = new Vec2(0.0f, 0.0f);
    }

    /**
     * My custom draw method.
     * Only I can call this function. Android calls onDraw() that does nothing.
     * This gives me more control over the drawing phase.
     * @param canvas The canvas to draw on.
     */
    public void doDraw(Canvas canvas)
    {
        // Color to clear the screen with.
        canvas.drawColor(Color.WHITE);

        canvas.save();

        // R.U.B.E. uses the positive y axis up orientation. It is just easier to flip everything.
        // But remember that the origin is now at the bottom left!
        //canvas.scale(1.0f, -1.0f, 0.0f, gameWorld.getDisplayMetrics().heightPixels / 2.0f);

        // Translate the viewport according to the player's position (camera pan).
        this.cameraPan(canvas);

        // Draw all the actors inside the set.
        synchronized (gameWorld)
        {
            gameWorld.drawActors(canvas);

            // Debug-draw the box2d world.
            if (gameWorld != null && gameWorld.getWorld() != null)
            {
                //DebugDraw.draw(gameWorld, canvas);
            }
            else
            {
                Log.v("Panel", "World was null!");
            }


            if (gameWorld.getLevel() != null)
                gameWorld.getLevel().drawMe(canvas);

            gameWorld.drawSpeechBubbles(canvas);
        }

        canvas.restore();

        DebugDraw.drawFramerate(gameWorld, canvas);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //The game has a fixed perspective: landscape.
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
            box2dThread = new Box2DThread(gameWorld);
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
        //new RandomObject(gameWorld, event.getX(), event.getY());

        gameWorld.getBlob().shoot( event.getX() - viewportTranslation.x,
                                   event.getY() - viewportTranslation.y );

        return super.onTouchEvent(event);
    }

    private void cameraPan(Canvas canvas)
    {
        if (gameWorld.getBlob() != null && gameWorld.getLevel() != null && gameWorld.getBlob().getBody() != null && gameWorld.getDisplayMetrics() != null)
        {
            // Horizontal pan.
            float dx = gameWorld.getBlob().getBody().getPosition().x * gameWorld.ratio() - gameWorld.getDisplayMetrics().widthPixels / 2.0f;

            if (dx < gameWorld.getLevel().getBoundingBox().left * gameWorld.ratio())
            {
                dx = gameWorld.getLevel().getBoundingBox().left * gameWorld.ratio();
            }
            else if (dx > gameWorld.getLevel().getBoundingBox().right * gameWorld.ratio() - gameWorld.getDisplayMetrics().widthPixels)
            {
                dx = gameWorld.getLevel().getBoundingBox().right * gameWorld.ratio() - gameWorld.getDisplayMetrics().widthPixels;
            }

            // Vertical pan.
            float dy = gameWorld.getBlob().getBody().getPosition().y * gameWorld.ratio() - gameWorld.getDisplayMetrics().heightPixels / 2.0f;

            if (dy < gameWorld.getLevel().getBoundingBox().top * gameWorld.ratio())
            {
                dy = gameWorld.getLevel().getBoundingBox().top * gameWorld.ratio();
            }
            else if (dy > gameWorld.getLevel().getBoundingBox().bottom * gameWorld.ratio() - gameWorld.getDisplayMetrics().heightPixels)
            {
                dy = gameWorld.getLevel().getBoundingBox().bottom * gameWorld.ratio() - gameWorld.getDisplayMetrics().heightPixels;
            }

            // Multiplying the distance by a number less than 1 will make for an ease-out effect when the camera pan gets to the destination.
            // The camera will move quickly when the distance is big and will slow down as the distance decreases.
            Vec2 move = new Vec2((viewportTranslation.x + dx) * 0.25f, (viewportTranslation.y + dy) * 0.25f);

            canvas.translate(viewportTranslation.x - move.x, viewportTranslation.y - move.y);

            viewportTranslation.set(viewportTranslation.x - move.x, viewportTranslation.y - move.y);
        }
    }
}
