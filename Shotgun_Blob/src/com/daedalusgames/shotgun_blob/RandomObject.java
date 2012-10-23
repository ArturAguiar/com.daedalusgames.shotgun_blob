package com.daedalusgames.shotgun_blob;

import org.jbox2d.collision.shapes.ShapeType;
import android.graphics.Matrix;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

/**
 * // -------------------------------------------------------------------------
/**
 *  Drop a random object into the world.
 *  For debug purposes.
 *
 *  @author Artur
 *  @version Oct 20, 2012
 */
public class RandomObject extends Actor
{
    /** The sprite for a crate. */
    private Bitmap crateSprite;

    /** The sprite for a ball. */
    private Bitmap ballSprite;

    /** The transformation matrix */
    private Matrix matrix;


    /**
     * Constructor that takes the position coordinates.
     * Right now this is just for testing.
     * @param x The horizontal coordinate.
     * @param y The vertical coordinate.
     * @param resources The project resources object.
     */
    public RandomObject(float x, float y, Resources resources)
    {
        crateSprite = BitmapFactory.decodeResource(resources, R.drawable.crate);
        crateSprite = Bitmap.createScaledBitmap(crateSprite,
                                                (int)(2.0f * Main.RATIO),
                                                (int)(2.0f * Main.RATIO),
                                                false);

        ballSprite = BitmapFactory.decodeResource(resources, R.drawable.ball);
        ballSprite = Bitmap.createScaledBitmap(ballSprite,
                                                (int)(2.0f * Main.RATIO),
                                                (int)(2.0f * Main.RATIO),
                                                false);

        matrix = new Matrix();

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
        matrix.reset();
        matrix.setTranslate((getBody().getPosition().x - 1.0f) * Main.RATIO,
                            (getBody().getPosition().y - 1.0f) * Main.RATIO);
        matrix.postRotate(getBody().getAngle() * 180.0f/(3.14159265f),
                          getBody().getPosition().x * Main.RATIO,
                          getBody().getPosition().y * Main.RATIO);

        if (getBody().getFixtureList().getShape().getType() == ShapeType.POLYGON)
        {
            canvas.drawBitmap(crateSprite, matrix, null);
        }
        else
        {
            canvas.drawBitmap(ballSprite, matrix, null);
        }

        /*
        canvas.drawBitmap(crateSprite, null,
                          new RectF((getBody().getPosition().x - 1.0f) * Main.RATIO,
                                    (getBody().getPosition().y - 1.0f) * Main.RATIO,
                                    (getBody().getPosition().x + 1.0f) * Main.RATIO,
                                    (getBody().getPosition().y + 1.0f) * Main.RATIO ),
                          null);
                          */
    }

    @Override
    public void charLogic()
    {
        //empty logic.
        //this is just a dummy object.
    }

}
