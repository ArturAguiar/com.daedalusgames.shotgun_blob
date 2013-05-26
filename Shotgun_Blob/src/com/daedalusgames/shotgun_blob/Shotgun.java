package com.daedalusgames.shotgun_blob;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

/**
 *  The class for Blob's shotgun. Contains its sprite and status that
 *  can change throughout the game.
 *
 *  @author Artur
 *  @version Dec 16, 2012
 */
public class Shotgun
{
    /** The game world reference. */
    private GameWorld gameWorld;

    /** The shotgun image. */
    private Bitmap shotgunSprite;

    /** The time for Blob to reload this shotgun. */
    private int reloadTime;

    /** The timer that indicates if the shotgun is reloaded or not.
     *  Values bigger than zero indicate that it is still reloading. */
    private int reloadCounter;

    /** The angle that the last shot was aimed at in degrees. */
    private float shotAt;

    /** The bitmap paint. */
    private Paint bitmapPaint;

    /** The image transformation matrix. */
    private Matrix matrix;

    /** The type of this shotgun. */
    public enum Type
    {
        /** Blob's first shotgun. */
        STANDARD
    }


    public Shotgun(GameWorld gameWorld, Shotgun.Type shotgunType)
    {
        this.gameWorld = gameWorld;

        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        matrix = new Matrix();

        switch (shotgunType)
        {
            case STANDARD:
                shotgunSprite = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.shotgun_sheet);
                reloadTime = 40;

                break;
        }


    }


    public void drawMe(Canvas canvas)
    {
        //Now draw the shotgun.
        matrix.reset();

        // The frame of the sprite to draw.
        // 0 = default; 1 = firing; 2 = reloading.
        int frame = 0;

        if (!gameWorld.getBlob().facingRight)
        {
            //flip the sprites if going left
            matrix.setScale(-1.0f, 1.0f);
        }
        else
        {
            matrix.setScale(1.0f, 1.0f);
        }

        // Move the sprite to the body's position.
        matrix.postTranslate(gameWorld.getBlob().getBody().getPosition().x * gameWorld.ratio(),
                             gameWorld.getBlob().getBody().getPosition().y * gameWorld.ratio() - shotgunSprite.getHeight() / 2.0f);


        if (shotAt != 0.0f)
        {
            // Recalculate the angle accounting for the horizontal flip.
            float angle = shotAt;
            if (angle > 90.0f)
            {
                angle = -180.0f + angle;
            }
            else if (angle < -90.0f)
            {
                angle = 180.0f + angle;
            }

            // Rotate the sprite by the calculated angle.
            matrix.postRotate(angle,
                              gameWorld.getBlob().getBody().getPosition().x * gameWorld.ratio(),
                              gameWorld.getBlob().getBody().getPosition().y * gameWorld.ratio());
        }


        if (reloadCounter > reloadTime - reloadTime / 10)
        {
            frame = 1;
        }
        else if (reloadCounter > 3 * reloadTime / 10)
        {
            frame = 0;
        }
        else if (reloadCounter > reloadTime / 10)
        {
            frame = 2;
        }


        canvas.drawBitmap(Bitmap.createBitmap(shotgunSprite, frame * shotgunSprite.getWidth() / 3, 0, shotgunSprite.getWidth() / 3, shotgunSprite.getHeight()), matrix, bitmapPaint);
    }


    /**
     * Shoots at the specified coordinate.
     * @param x The x coordinate to shoot at.
     * @param y The y coordinate to shoot at.
     */
    public boolean shoot(float x, float y)
    {
        // Only shoot if completely reloaded.
        if (reloadCounter > 0)
        {
            return false;
        }

        float angle = (float)Math.atan2(y - gameWorld.getBlob().getBody().getPosition().y * gameWorld.ratio(),
                                        x - gameWorld.getBlob().getBody().getPosition().x * gameWorld.ratio());

        shotAt = angle * 180.0f/(3.14159265f);
        reloadCounter = reloadTime;

        return true;
    }


    public void logic()
    {
        if (reloadCounter > 0)
        {
            // Decrement the timer.
            reloadCounter--;
            if (reloadCounter == 0)
            {
                // Reset to default angle if ready to shoot again.
                shotAt = 0.0f;
            }
        }
    }


    public float getShotAt()
    {
        return shotAt;
    }
}
