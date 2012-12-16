package com.daedalusgames.shotgun_blob;

import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
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

    /** the joint helpers. */
    private Body[] jointHelpers;

    /** Blob's health */
    private int health;

    /** The current movement speed of blob. */
    private float moveSpeed;

    /** The maximum movement speed of blob. */
    private float maxSpeed;

    /** The number of adjacent circles to simulate a soft body */
    //Multiple of 3, please. At least 6. I advise 12.
    private final int NUM_CIRCLES = 12;

    /** The shotgun image. */
    private Bitmap shotgunSprite;

    /** The eyes sprite. */
    private Bitmap eyeSprite;

    /** The image transformation matrix. */
    private Matrix matrix;

    /** The angle that the last shot was aimed at in degrees. */
    private float shotAt;

    /** The timer that indicates if the shotgun is reloaded or not.
     *  Values bigger than zero indicate that it is still reloading. */
    private int reloadTimer;

    /** The time for Blob to reload his shotgun. */
    private int reloadTime;

    /** The bitmap paint. */
    private Paint bitmapPaint;


    /**
     * Blob constructor.
     * @param myGameWorld A reference to the game world.
     */
    public Blob(GameWorld myGameWorld)
    {
        //Set the value of the gameWorld reference inherited from Actor.
        gameWorld = myGameWorld;

        setType(ActorType.BLOB);

        health = 100;

        maxSpeed = 8.0f;
        moveSpeed = 0.0f;

        adjCircles = new Body[NUM_CIRCLES];

        jointHelpers = new Body[NUM_CIRCLES / 3];

        matrix = new Matrix();


        shotgunSprite = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.shotgun_sheet);

        eyeSprite = BitmapFactory.decodeResource(gameWorld.getResources(), R.drawable.eyes);
        eyeSprite = Bitmap.createScaledBitmap(eyeSprite, 40, 26, false);


        reloadTimer = 0;
        reloadTime = 40;

        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        //Push the Actor to the toBeCreated set so that the physical entities are added safely.
        gameWorld.queueActor(this);
    }

    /**
     * Constructor that takes the position coordinates of blob.
     * @param myGameWorld A reference to the game world.
     * @param x The horizontal coordinate in pixels.
     * @param y The vertical coordinate in pixels.
     */
    public Blob(GameWorld myGameWorld, float x, float y)
    {
        this(myGameWorld);
        this.setInitialPosition(new Vec2(x / gameWorld.ratio(), y / gameWorld.ratio()));
    }

    @Override
    public void createEntity()
    {
        //The shape of the body
        CircleShape shape = new CircleShape();
        shape.m_radius = 0.5f;

        //The physical characteristics of the body.
        FixtureDef fixtDef = new FixtureDef();
        fixtDef.density = 0.9f;
        fixtDef.restitution = 0.3f;
        fixtDef.shape = shape;
        fixtDef.friction = 1.0f;


        //BodyDef inherited from actor.
        setBodyDef(new BodyDef());
        getBodyDef().type = BodyType.DYNAMIC;
        getBodyDef().fixedRotation = true;

        if ( getInitialPosition() != null)
        {
            getBodyDef().position = new Vec2(getInitialPosition().x,
                                             getInitialPosition().y);
        }
        else
        {
            getBodyDef().position = new Vec2( (gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(),
                                              (gameWorld.getDisplayMetrics().heightPixels / 2.0f) / gameWorld.ratio() );
        }

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
            //bd.bullet = true;
            //bd.fixedRotation = true;
            bd.angularDamping = 30.0f;
            bd.position = new Vec2(getBodyDef().position.x + 1.5f * FloatMath.cos(2.0f*(3.14592f) * i/NUM_CIRCLES),
                                   getBodyDef().position.y + 1.5f * FloatMath.sin(2.0f*(3.14592f) * i/NUM_CIRCLES));

            adjCircles[i] = gameWorld.getWorld().createBody( bd );
            adjCircles[i].createFixture(fixtDef).setUserData(this); // Set a reference to this actor as user data.

            cvjd.addBody(adjCircles[i]);

            //The wheel joint would be perfect here, but it has not yet been ported to jBox2D,
            //so this leaves us having to create two joints and two bodies.
            //Maybe I could port it........ nah.
            if ( NUM_CIRCLES / 4 > 0 && i % (NUM_CIRCLES / 4) == 0 )
            {
                PolygonShape boxShape = new PolygonShape();
                boxShape.setAsBox(0.2f, 0.2f);

                fixtDef.shape = boxShape;
                jointHelpers[i / 3] = gameWorld.getWorld().createBody(bd);
                jointHelpers[i / 3].createFixture(fixtDef).setUserData(this); // Set a reference to this actor as user data.

                //Connect the adjacent joint helper to the inner circle with a prismatic joint.
                //This will only allow for one axis of movement.
                PrismaticJointDef jointDef = new PrismaticJointDef();

                jointDef.initialize(jointHelpers[i / 3], getBody(),
                                    getBody().getWorldCenter(),
                                    new Vec2(FloatMath.cos(2.0f*(3.14592f) * i/NUM_CIRCLES),
                                             FloatMath.sin(2.0f*(3.14592f) * i/NUM_CIRCLES) ));
                jointDef.collideConnected = true;
                jointDef.maxMotorForce = 15.0f;
                jointDef.motorSpeed = 10.0f;
                //jointDef.enableMotor = true;

                gameWorld.getWorld().createJoint(jointDef);


                //Connect the adjacent circle to the joint helper so that it can rotate.
                RevoluteJointDef revJointDef = new RevoluteJointDef();
                revJointDef.initialize(adjCircles[i], jointHelpers[i / 3], adjCircles[i].getWorldCenter());
                jointDef.collideConnected = false;

                gameWorld.getWorld().createJoint(revJointDef);
            }
        }

        cvjd.frequencyHz = 20.0f;
        cvjd.dampingRatio = 25.0f;
        gameWorld.getWorld().createJoint(cvjd);

        //Add Blob into the actors set now that it is complete.
        gameWorld.pushActor(this);
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



        //Now draw the shotgun.
        matrix.reset();

        // The frame of the sprite to draw.
        // 0 = default; 1 = firing; 2 = reloading.
        int frame = 0;

        if (!facingRight)
        //if ( Math.abs(shotAt) > 90.0f || (shotAt == 0 && gameWorld.getGravity().y < -0.5f) )
        {
            //flip the sprites if going left
            matrix.setScale(-1.0f, 1.0f);
        }
        else
        {
            matrix.setScale(1.0f, 1.0f);
        }

        // Move the sprite to the body's position.
        matrix.postTranslate(getBody().getPosition().x * gameWorld.ratio(),
                             getBody().getPosition().y * gameWorld.ratio() - shotgunSprite.getHeight() / 2.0f);


        canvas.drawBitmap(eyeSprite, matrix, bitmapPaint);


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
                              getBody().getPosition().x * gameWorld.ratio(),
                              getBody().getPosition().y * gameWorld.ratio());
        }


        if (reloadTimer > reloadTime - reloadTime / 10)
        {
            frame = 1;
        }
        else if (reloadTimer > 3 * reloadTime / 10)
        {
            frame = 0;
        }
        else if (reloadTimer > reloadTime / 10)
        {
            frame = 2;
        }

        if (reloadTimer > 0)
        {
            // Decrement the timer.
            reloadTimer--;
            if (reloadTimer == 0)
            {
                // Reset to default angle if ready to shoot again.
                shotAt = 0.0f;
            }
        }


        canvas.drawBitmap(Bitmap.createBitmap(shotgunSprite, frame * shotgunSprite.getWidth() / 3, 0, shotgunSprite.getWidth() / 3, shotgunSprite.getHeight()), matrix, bitmapPaint);
    }

    @Override
    public void charLogic()
    {
        //Set the speed to the new value.
        moveSpeed = gameWorld.getGravity().y;

        if(Math.abs(shotAt) > 90.0f || (shotAt == 0 && gameWorld.getGravity().y < -0.5f))
        {
            facingRight = false;
        }
        else
        {
            facingRight = true;
        }

        //A comfortable dead-zone.
        if (moveSpeed > -0.5f && moveSpeed < 0.5f)
        {
            moveSpeed = 0;
        }

        //Execute an action if it is set.
        if (this.getActionToPerform() != null)
        {
            this.executeAction(this.getActionToPerform());

            if(this.getActionToPerform() != null &&
               this.getActionToPerform().restrains())
            {
                moveSpeed = 0;
            }
        }


        //Cap the movement speed at maxSpeed.
        else if (moveSpeed > maxSpeed)
        {
            moveSpeed = maxSpeed;
        }
        else if (moveSpeed < -maxSpeed)
        {
            moveSpeed = -maxSpeed;
        }

        //Apply the velocity.
        getBody().setLinearVelocity(new Vec2(getBody().getLinearVelocity().x + moveSpeed, getBody().getLinearVelocity().y));

        if ( moveSpeed > 0 && getBody().getLinearVelocity().x > maxSpeed )
        {
            getBody().setLinearVelocity( new Vec2( maxSpeed , getBody().getLinearVelocity().y ) );
        }
        else if ( moveSpeed < 0 && getBody().getLinearVelocity().x < -maxSpeed )
        {
            getBody().setLinearVelocity( new Vec2( -maxSpeed , getBody().getLinearVelocity().y ) );
        }
    }

    /**
     * Shoots at the specified coordinate.
     * @param x The x coordinate to shoot at.
     * @param y The y coordinate to shoot at.
     */
    public void shoot(float x, float y)
    {
        // Only shoot if completely reloaded and not making a restraining action.
        if (reloadTimer > 0 || (this.getActionToPerform() != null && this.getActionToPerform().restrains()))
        {
            return;
        }

        float angle = (float)Math.atan2(y - getBody().getPosition().y * gameWorld.ratio(),
                                        x - getBody().getPosition().x * gameWorld.ratio());

        Vec2 velocity = new Vec2(-15.0f * FloatMath.cos(angle),
                                 -15.0f * FloatMath.sin(angle));

        getBody().setLinearVelocity(velocity);

        for (int i = 0; i < adjCircles.length; i++)
        {
            adjCircles[i].setLinearVelocity(velocity);
        }

        shotAt = angle * 180.0f/(3.14159265f);
        reloadTimer = reloadTime;
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

    /**
     * Returns the maximum speed of blob.
     * @return Blob's maximum speed.
     */
    public float getMaxSpeed()
    {
        return maxSpeed;
    }

    /**
     * Returns blob's reload timer.
     *
     * @return The reload timer.
     */
    public int getReloadTimer()
    {
        return reloadTimer;
    }

}
