package com.daedalusgames.shotgun_blob;

import android.graphics.Matrix;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import android.graphics.Path;
import android.util.FloatMath;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.FixtureDef;
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
    /** The surrounding circle bodies */
    private Body[] adjCircles;

    /** Blob's health */
    private int health;

    /** The number of adjacent circles to simulate a soft body */
    //Multiple of 3, please. At least 6. I advise for 12.
    private final int NUM_CIRCLES = 12;

    /** The shotgun image. */
    private Bitmap shotgunSprite;

    /** The image transformation matrix. */
    private Matrix matrix;


    /**
     * Blob constructor.
     * @param myGameWorld A reference to the game world.
     */
    public Blob(GameWorld myGameWorld)
    {
        //Set the value of the gameWorld reference inherited from Actor.
        gameWorld = myGameWorld;

        adjCircles = new Body[NUM_CIRCLES];

        health = 100;

        shotgunSprite = BitmapFactory.decodeResource(myGameWorld.getResources(), R.drawable.shotgun);

        matrix = new Matrix();

        //The shape of the body
        CircleShape shape = new CircleShape();
        shape.m_radius = 0.5f;

        //The physical characteristics of the body.
        FixtureDef fixtDef = new FixtureDef();
        fixtDef.density = 0.9f;
        fixtDef.restitution = 0.3f;
        fixtDef.shape = shape;
        fixtDef.friction = 0.4f;


        //BodyDef inherited from actor.
        setBodyDef(new BodyDef());
        getBodyDef().type = BodyType.DYNAMIC;
        getBodyDef().fixedRotation = true;
        getBodyDef().position = new Vec2( (gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(),
                                          (gameWorld.getDisplayMetrics().heightPixels / 2.0f) / gameWorld.ratio() );


        //Apply these characteristics to the body and add it to the world.
        setBody( gameWorld.getWorld().createBody( getBodyDef() ) );
        getBody().createFixture(fixtDef); //add the shape

        ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();


        //Create the adjacent circles and add them to the array.
        for (int i = 0; i < NUM_CIRCLES; i++)
        {
            shape.m_radius = 0.3f;
            fixtDef.shape = shape;
            fixtDef.density = 1.5f;
            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.bullet = true;
            bd.fixedRotation = true;
            bd.position = new Vec2(getBodyDef().position.x + 1.5f * FloatMath.cos(2.0f*(3.14592f) * i/NUM_CIRCLES),
                                   getBodyDef().position.y + 1.5f * FloatMath.sin(2.0f*(3.14592f) * i/NUM_CIRCLES));

            adjCircles[i] = gameWorld.getWorld().createBody( bd );
            adjCircles[i].createFixture(fixtDef);

            cvjd.addBody(adjCircles[i]);

            if ( NUM_CIRCLES / 4 > 0 && i % (NUM_CIRCLES / 4) == 0 )
            {
                //Connect the adjacent circle to the center circle.
                DistanceJointDef jointDef = new DistanceJointDef();

                jointDef.initialize(adjCircles[i], getBody(),
                                    adjCircles[i].getWorldCenter(),
                                    getBody().getWorldCenter());
                jointDef.collideConnected = true;
                jointDef.frequencyHz = 2.0f;
                jointDef.dampingRatio = 2.0f;

                gameWorld.getWorld().createJoint(jointDef);
            }
        }

        //DistanceJointDef jointDef = new DistanceJointDef();

        //Create all the joints.
        /*
        for (int i = 0; i < NUM_CIRCLES; i++)
        {
            Body currentBody = adjCircles[i];
            Body neighborBody;

            //Wrap around the circle back to the first at index 0.
            if (i + 1 < NUM_CIRCLES)
            {
                neighborBody = adjCircles[i+1];
            }
            else
            {
                neighborBody = adjCircles[0];
            }

            //Connect the outer circles to each other.
            jointDef.initialize(currentBody, neighborBody,
                                currentBody.getWorldCenter(),
                                neighborBody.getWorldCenter() );
            jointDef.collideConnected = true;
            jointDef.frequencyHz = 10.0f;
            jointDef.dampingRatio = 0.5f;

            Main.getWorld().createJoint(jointDef);

            //Connect the adjacent circle to the center circle.
            jointDef.initialize(currentBody, getBody(),
                                currentBody.getWorldCenter(),
                                getBody().getWorldCenter());
            jointDef.collideConnected = true;
            jointDef.frequencyHz = 4.5f;
            jointDef.dampingRatio = 0.5f;

            Main.getWorld().createJoint(jointDef);
        }
        */

        cvjd.frequencyHz = 20.0f;
        cvjd.dampingRatio = 25.0f;
        gameWorld.getWorld().createJoint(cvjd);


        //Add Blob into the actors set.
        gameWorld.pushActor(this);
    }

    /**
     * Constructor that takes the position coordinates of blob.
     * @param myGameWorld A reference to the game world.
     * @param x The horizontal coordinate.
     * @param y The vertical coordinate.
     */
    public Blob(GameWorld myGameWorld, float x, float y)
    {
        this(myGameWorld);
        getBodyDef().position = new Vec2(x / gameWorld.ratio(), y / gameWorld.ratio());
    }

    @Override
    public void drawMe(Canvas canvas)
    {
        Path path = new Path();

        //Go to the first adjacent circle and start from there.
        path.moveTo(adjCircles[0].getPosition().x * gameWorld.ratio(), adjCircles[0].getPosition().y * gameWorld.ratio());

        //Go through all adjacent circles applying some awesome math to create
        //a smooth bezier curve around them.
        //Thanks Dr. Tony Allevato for this!
        //The number of circles has to be a multiple of 3 for this to work!
        for (int i = 0; i < adjCircles.length;)
        {
            Vec2 q0 = new Vec2(adjCircles[i].getPosition().x * gameWorld.ratio(), adjCircles[i].getPosition().y * gameWorld.ratio());
            i++;
            Vec2 q1 = new Vec2(adjCircles[i].getPosition().x * gameWorld.ratio(), adjCircles[i].getPosition().y * gameWorld.ratio());
            i++;
            Vec2 q2 = new Vec2(adjCircles[i].getPosition().x * gameWorld.ratio(), adjCircles[i].getPosition().y * gameWorld.ratio());
            i++;
            Vec2 q3 = new Vec2();
            if (i < adjCircles.length)
            {
                q3 = new Vec2(adjCircles[i].getPosition().x * gameWorld.ratio(), adjCircles[i].getPosition().y * gameWorld.ratio());
            }
            else
            {
                q3 = new Vec2(adjCircles[0].getPosition().x * gameWorld.ratio(), adjCircles[0].getPosition().y * gameWorld.ratio());
            }

            Vec2 p1 = new Vec2();
            Vec2 p2 = new Vec2();

            // P1 = (1/6)(-5 Q0 + 18 Q1 - 9 Q2 + 2 Q3)
            p1.x = (1.0f/6.0f)*( -5.0f*q0.x + 18.0f*q1.x - 9.0f*q2.x + 2.0f*q3.x );
            p1.y = (1.0f/6.0f)*( -5.0f*q0.y + 18.0f*q1.y - 9.0f*q2.y + 2.0f*q3.y );

            // P2 = (1/6)(2 Q0 - 9 Q1 + 18 Q2 - 5 Q3)
            p2.x = (1.0f/6.0f)*( 2.0f*q0.x - 9.0f*q1.x + 18.0f*q2.x - 5.0f*q3.x );
            p2.y = (1.0f/6.0f)*( 2.0f*q0.y - 9.0f*q1.y + 18.0f*q2.y - 5.0f*q3.y );

            path.cubicTo(p1.x, p1.y, p2.x, p2.y, q3.x, q3.y);

        }

        Paint paint = new Paint();
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(19.0f);
        paint.setAntiAlias(true);

        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(16.0f);

        canvas.drawPath(path, paint);

        //Now draw the brain and the shotgun.
        matrix.reset();

        if ( gameWorld.getGravity().y < -0.5f )
        {
            //flip the sprites if going left
            matrix.setScale(-1.0f, 1.0f);
        }
        else
        {
            matrix.setScale(1.0f, 1.0f);
        }

        matrix.postTranslate(getBody().getPosition().x * gameWorld.ratio(),
                             getBody().getPosition().y * gameWorld.ratio() - 15.0f);

        canvas.drawBitmap(shotgunSprite, matrix, null);
    }

    @Override
    public void charLogic()
    {
        float moveSpeed = gameWorld.getGravity().y;

        //Cap the movement speed at 4.
        if (moveSpeed > 4.0f)
        {
            moveSpeed = 4.0f;
        }
        else if (moveSpeed < -4.0f)
        {
            moveSpeed = -4.0f;
        }

        //This is still not correct. I am overriding the x component of velocity.
        getBody().setLinearVelocity(new Vec2(moveSpeed, getBody().getLinearVelocity().y));
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
