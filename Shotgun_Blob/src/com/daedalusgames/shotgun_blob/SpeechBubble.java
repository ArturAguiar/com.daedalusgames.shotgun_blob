package com.daedalusgames.shotgun_blob;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Rect;
import java.util.ArrayList;
import org.jbox2d.common.Vec2;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Canvas;
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

    private Bitmap fullBubble = null;

    private Vec2 position;

    private Paint bubbleFillPaint;
    private Paint bubbleStrokePaint;
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

        position = new Vec2();

        this.bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        this.bubbleFillPaint = new Paint();
        bubbleFillPaint.setStyle(Paint.Style.FILL);
        bubbleFillPaint.setColor(Color.parseColor("#F9F9F9"));
        bubbleFillPaint.setAntiAlias(true);

        this.bubbleStrokePaint = new Paint();
        bubbleStrokePaint.setStyle(Paint.Style.STROKE);
        bubbleStrokePaint.setStrokeWidth(2.0f);
        bubbleStrokePaint.setColor(Color.parseColor("#000000"));
        bubbleStrokePaint.setAntiAlias(true);

        this.textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(18);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.createFromAsset(gameWorld.getResources().getAssets(), "visitor.ttf"));

        this.talker = talker;

        this.text = new ArrayList<String>();

        // Split the text into ~20 character lines, but only split on whitespace.
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


        // Generates the static bitmap for the speech bubble and store it in fullBubble.
        Rect textBounds = new Rect();
        textPaint.getTextBounds("A", 0, 1, textBounds);

        float lineHeight = textBounds.height();

        float textWidth = 0.0f;
        for (int i = 0; i < text.size(); i++)
        {
            if (textPaint.measureText(text.get(i)) > textWidth)
            {
                textWidth = textPaint.measureText(text.get(i));
            }
        }

        fullBubble = Bitmap.createBitmap((int)(textWidth + 30.0f), (int)((lineHeight + 5.0f) * text.size() + 45.0f), Bitmap.Config.ARGB_8888);

        Canvas tempCanvas = new Canvas(fullBubble);

        tempCanvas.drawRoundRect(new RectF(0.0f, 0.0f, fullBubble.getWidth(), fullBubble.getHeight() -20.0f), 20, 20, bubbleFillPaint);
        tempCanvas.drawRoundRect(new RectF(1.0f, 1.0f, fullBubble.getWidth() - 1.0f, fullBubble.getHeight() - 21.0f), 20, 20, bubbleStrokePaint);

        for (int i = 0; i < text.size(); i++)
        {
            tempCanvas.drawText(text.get(i), 15.0f + textWidth / 2.0f - textPaint.measureText(text.get(i)) / 2.0f, 10.0f + (lineHeight + 5.0f) * (i + 1), textPaint);
        }

        Path pointer = new Path();
        pointer.moveTo(this.position.x + fullBubble.getWidth() / 2.0f - 20.0f, this.position.y + fullBubble.getHeight() - 22.0f);
        pointer.lineTo(this.position.x + fullBubble.getWidth() / 2.0f, this.position.y + fullBubble.getHeight() - 1.0f);
        pointer.lineTo(this.position.x + fullBubble.getWidth() / 2.0f + 20.0f, this.position.y + fullBubble.getHeight() - 22.0f);

        tempCanvas.drawPath(pointer, bubbleFillPaint);
        tempCanvas.drawPath(pointer, bubbleStrokePaint);
    }

    /**
     * This method draws this speech bubble on the provided canvas.
     * @param canvas The canvas to draw on.
     */
    public void drawMe(Canvas canvas)
    {
        this.position.set(
            talker.getBody().getPosition().x * gameWorld.ratio() - fullBubble.getWidth() / 2.0f,
            talker.getBody().getPosition().y * gameWorld.ratio() - fullBubble.getHeight() - 60.0f);

        canvas.drawBitmap(fullBubble, position.x, position.y, bitmapPaint);
    }
}
