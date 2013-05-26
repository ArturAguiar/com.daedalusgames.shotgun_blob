package com.daedalusgames.shotgun_blob;

import org.jbox2d.common.Vec2;
import android.graphics.Paint;
import org.jbox2d.dynamics.Body;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class LevelObject extends Actor
{
    private Bitmap sprite;

    private Paint bitmapPaint;

    private Vec2 bitmapOffset;

    public LevelObject(GameWorld myGameWorld, Body actorBody, Bitmap mySprite)
    {
        this.gameWorld = myGameWorld;
        this.setBody(actorBody);
        this.sprite = mySprite;

        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bitmapOffset = new Vec2(0.0f, 0.0f);

        gameWorld.pushActor(this);
    }

    public LevelObject(GameWorld myGameWorld, Body actorBody, Bitmap mySprite, Vec2 bitmapOffset)
    {
        this(myGameWorld, actorBody, mySprite);

        this.bitmapOffset = bitmapOffset;
    }

    @Override
    public void createEntity()
    {
        // The entity is already created when the level loads.
    }


    @Override
    public void drawMe(Canvas canvas)
    {
        if (this.sprite == null || this.getBody() == null)
            return;

        canvas.drawBitmap(sprite, this.getBody().getPosition().x * gameWorld.ratio(), this.getBody().getPosition().y * gameWorld.ratio(), bitmapPaint);
    }


    @Override
    public void charLogic()
    {
        // TODO Auto-generated method stub
    }

}
