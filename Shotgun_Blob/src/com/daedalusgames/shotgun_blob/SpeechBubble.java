package com.daedalusgames.shotgun_blob;

import android.util.FloatMath;
import java.util.ArrayList;
import org.jbox2d.common.Vec2;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

/**
 *  A speech bubble that contains the specified text and pops up above the
 *  actor.
 *
 *  @author Artur
 *  @version Dec 15, 2012
 */
public class SpeechBubble
{
    private GameWorld gameWorld;

    private Bitmap bubbleBG;

    private Paint bitmapPaint;
    private Paint textPaint;

    private Actor talker;

    private ArrayList<String> text;

    /**
     * Initializes all fields.
     * @param gameWorld The gameWorld reference.
     * @param talker The actor that is talking.
     * @param speech The text to be displayed inside this bubble.
     */
    public SpeechBubble(GameWorld gameWorld, Actor talker, String speech)
    {
        this.gameWorld = gameWorld;

        bubbleBG = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.speech_bubble);

        this.bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        this.textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.MONOSPACE);

        this.talker = talker;

        this.text = new ArrayList<String>();

        // Split into ~20 character lines, but only split on whitespace.
        int amountOfChars = 20;

        String toSplit = speech;

        while(toSplit.length() > amountOfChars)
        {
            int end;
            if (amountOfChars < toSplit.length())
            {
                end = toSplit.substring(0, amountOfChars).lastIndexOf(' ');
            }
            else
            {
                end = toSplit.substring(0, toSplit.length()).lastIndexOf(' ');
            }

            if (end > -1)
            {
                text.add(toSplit.substring(0, end));
            }
            else
            {
                text.add(toSplit.substring(0, amountOfChars));
            }

            toSplit = toSplit.substring(end + 1, toSplit.length());
        }

        if (toSplit.length() > 0)
        {
            text.add(toSplit);
        }

    }

    /**
     * This method draws this speech bubble on the provided canvas.
     * @param canvas The canvas to draw on.
     */
    public void drawMe(Canvas canvas)
    {
        Vec2 position = new Vec2(
            talker.getBody().getPosition().x * gameWorld.ratio() + 30.0f,
            talker.getBody().getPosition().y * gameWorld.ratio() - this.bubbleBG.getHeight() - 30.0f);

        canvas.drawBitmap(bubbleBG,
                          position.x,
                          position.y,
                          bitmapPaint);

        position.x = position.x + bubbleBG.getWidth() / 2.0f;
        position.y = position.y + 16.0f + bubbleBG.getHeight() / 2.0f - 12.0f * text.size();

        for (int i = 0; i < text.size(); i++)
        {
            canvas.drawText(text.get(i), FloatMath.floor(position.x - 3.5f * text.get(i).length()), FloatMath.floor(position.y + 12.0f * i), textPaint);
        }

    }
}
