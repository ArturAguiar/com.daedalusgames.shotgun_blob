package com.daedalusgames.shotgun_blob;

import org.jbox2d.collision.shapes.ShapeType;
import android.graphics.Matrix;
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
     * @param myGameWorld The game world reference.
     * @param x The horizontal coordinate.
     * @param y The vertical coordinate.
     */
    public RandomObject(GameWorld myGameWorld, float x, float y)
    {
        //inherited from Actor.
        gameWorld = myGameWorld;

        crateSprite = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.crate);
        crateSprite = Bitmap.createScaledBitmap(crateSprite,
                                                (int)(2.0f * gameWorld.ratio()),
                                                (int)(2.0f * gameWorld.ratio()),
                                                false);

        ballSprite = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.ball);
        ballSprite = Bitmap.createScaledBitmap(ballSprite,
                                                (int)(2.0f * gameWorld.ratio()),
                                                (int)(2.0f * gameWorld.ratio()),
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
        getBodyDef().position = new Vec2(x / gameWorld.ratio(), y / gameWorld.ratio());
        setBody( gameWorld.getWorld().createBody( getBodyDef() ) );

        if (getBody() != null)
        {
            if (gameWorld.getWorld().getBodyCount() % 2 == 1)
            {
                Fixture fixt = getBody().createFixture(circleShape, 1.0f);
                fixt.m_restitution = 0.3f;
                gameWorld.pushActor(this);
            }
            else
            {
                Fixture fixt = getBody().createFixture(polyShape, 1.0f);
                fixt.m_restitution = 0.3f;
                gameWorld.pushActor(this);
            }

        }
    }

    @Override
    public void drawMe(Canvas canvas)
    {
        matrix.reset();
        matrix.setTranslate((getBody().getPosition().x - 1.0f) * gameWorld.ratio(),
                            (getBody().getPosition().y - 1.0f) * gameWorld.ratio());
        matrix.postRotate(getBody().getAngle() * 180.0f/(3.14159265f),
                          getBody().getPosition().x * gameWorld.ratio(),
                          getBody().getPosition().y * gameWorld.ratio());

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
                          new RectF((getBody().getPosition().x - 1.0f) * gameWorld.ratio(),
                                    (getBody().getPosition().y - 1.0f) * gameWorld.ratio(),
                                    (getBody().getPosition().x + 1.0f) * gameWorld.ratio(),
                                    (getBody().getPosition().y + 1.0f) * gameWorld.ratio() ),
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
