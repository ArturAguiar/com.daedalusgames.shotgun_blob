package com.daedalusgames.shotgun_blob;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Fixture;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

/**
 * // -------------------------------------------------------------------------
/**
 *  The class for the main character, Blob.
 *
 *  @author Artur
 *  @version Sep 24, 2012
 */
public class Blob extends Actor
{
    /** Blob's health */
    private int health;


    /**
     * Blob constructor.
     */
    public Blob()
    {
        health = 100;

        //Body inherited from actor
        CircleShape shape = new CircleShape();
        shape.m_radius = 1.0f;

        //BodyDef inherited from actor.
        setBodyDef(new BodyDef());
        getBodyDef().type = BodyType.DYNAMIC;
        getBodyDef().position = new Vec2( (Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO,
                                          (Main.displayMetrics.heightPixels / 2.0f) / Main.RATIO );
        setBody( Main.getWorld().createBody( getBodyDef() ) );

        Fixture fixt = getBody().createFixture(shape, 1.0f);
        fixt.m_restitution = 0.3f;

        //Add Blob into the actors set.
        Main.pushActor(this);
    }

    /**
     * Constructor that takes the position coordinates of blob.
     * Right now this is just for testing.
     * @param x The horizontal coordinate.
     * @param y The vertical coordinate.
     */
    public Blob(float x, float y)
    {
        health = 100;

        //Body inherited from actor
        CircleShape circleShape = new CircleShape();
        circleShape.m_radius = 1.0f;

        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(1.0f, 1.0f);

        //BodyDef inherited from actor.
        setBodyDef( new BodyDef() );
        getBodyDef().type = BodyType.DYNAMIC;
        getBodyDef().position = new Vec2(x / Main.RATIO, y / Main.RATIO);
        setBody( Main.getWorld().createBody( getBodyDef() ) );

        if (getBody() != null)
        {
            if (Main.getWorld().getBodyCount() % 2 == 1)
            {
                Fixture fixt = getBody().createFixture(circleShape, 1.0f);
                fixt.m_restitution = 0.3f;
                Main.pushActor(this);
            }
            else
            {
                Fixture fixt = getBody().createFixture(polyShape, 1.0f);
                fixt.m_restitution = 0.3f;
                Main.pushActor(this);
            }

        }
    }

    @Override
    public void drawMe(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        canvas.drawCircle(getBody().getPosition().x * Main.RATIO,
                          getBody().getPosition().y * Main.RATIO,
                          1.0f * Main.RATIO,
                          paint);
    }

    /**
     * Health getter.
     * @return Blob's health.
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * Health setter.
     * @param newHealth the new value for the health.
     */
    public void setHealth(int newHealth)
    {
        if (newHealth >= 0 && newHealth <= 100)
        {
            health = newHealth;
        }
    }

}
