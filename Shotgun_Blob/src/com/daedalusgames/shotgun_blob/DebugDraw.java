package com.daedalusgames.shotgun_blob;

import java.text.DecimalFormat;
import android.graphics.Typeface;
import org.jbox2d.dynamics.joints.Joint;
import android.graphics.Path;
import org.jbox2d.collision.shapes.PolygonShape;
import android.graphics.Canvas;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.BodyType;
import android.graphics.Color;
import android.graphics.Paint;
import org.jbox2d.dynamics.Fixture;
import android.util.FloatMath;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * // -------------------------------------------------------------------------
/**
 *  My custom Debug Draw class that uses the Android canvas to draw
 *  all shapes in the JBox2D world.
 *
 *  @author Artur
 *  @version Oct 2, 2012
 */
public class DebugDraw
{
    /** Flag to tell the debug drawer to also draw joints. True by default. */
    public static boolean DRAW_JOINTS = true;

    /** Flag to tell the debug drawer to also draw the FPS. True by default. */
    public static boolean SHOW_FRAMERATE = true;

    /**
     * Method that draws all shapes in the given world in debug mode.
     * @param gameWorld The game world to be drawn.
     * @param canvas The screen canvas to draw to.
     */
    public static void draw(GameWorld gameWorld, Canvas canvas)
    {
        World world = gameWorld.getWorld();

        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);


        //Iterate through the list of bodies in the world to draw them.
        for (Body body = world.getBodyList(); body != null; body = body.getNext())
        {
            //Vec2 bodyCenter = body.getPosition();
            float bodyRotation = body.getAngle();

            Transform xForm = body.getTransform();

            //Get the x and y components of the rotation.
            //Found out that android has its own cos() and sin() methods for floats,
            //which Java doesn't. I like android. :)
            //This will prevent any conversion errors.
            Vec2 axis = new Vec2( FloatMath.cos(bodyRotation), FloatMath.sin(bodyRotation) );


            //Now iterate through every fixture inside the body and draw it according to its shape.
            for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext())
            {
                //Choose color based on type of body.
                //I'm trying to follow box2d color conventions here.
                if (fixture.isSensor())
                {
                    //Set the color of sensors to yellow independent of type.
                    paint.setARGB(200, 230, 230, 0);

                }
                else if (body.getType() == BodyType.DYNAMIC)
                {
                    //semi-transparent gray.
                    if (body.isAwake())
                    {
                        paint.setARGB(200, 200, 200, 200);
                    }
                    else
                    {
                        paint.setARGB(200, 160, 160, 160);
                    }
                }
                else if (body.getType() == BodyType.STATIC)
                {
                    //semi-transparent green.
                    paint.setARGB(200, 128, 204, 128);
                }
                else if (body.getType() == BodyType.KINEMATIC)
                {
                    //semi-transparent purple.
                    paint.setARGB(200, 204, 77, 204);
                }

                //Now draw the shape based on the shape type.
                if (fixture.getType() == ShapeType.CIRCLE)
                {
                    paint.setStyle(Paint.Style.FILL);

                    CircleShape circle = (CircleShape)fixture.getShape();

                    //OK, I'm not gonna pretend that I know what mul() does.
                    //Searched around the docs a lot. Couldn't find a satisfactory
                    //answer. But it works.
                    //I believe that it applies the transformations (like rotation)
                    //to the vertex coordinates.
                    Vec2 circleCenter = Transform.mul(xForm, circle.m_p );

                    float radius = circle.m_radius;

                    //Draw the circle fill.
                    canvas.drawCircle(circleCenter.x * gameWorld.ratio(),
                                      circleCenter.y * gameWorld.ratio(),
                                      radius * gameWorld.ratio(),
                                      paint);

                    //Draw the circle stroke (edge).
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(circleCenter.x * gameWorld.ratio(),
                                      circleCenter.y * gameWorld.ratio(),
                                      radius * gameWorld.ratio(),
                                      paint);

                    //Draw a radius so that the rotation becomes noticeable.
                    canvas.drawLine(circleCenter.x * gameWorld.ratio(),
                                    circleCenter.y * gameWorld.ratio(),
                                    (circleCenter.x + radius * axis.x) * gameWorld.ratio(),
                                    (circleCenter.y + radius * axis.y) * gameWorld.ratio(),
                                    paint);
                }

                else if (fixture.getType() == ShapeType.POLYGON)
                {
                    paint.setStyle(Paint.Style.FILL);

                    PolygonShape polygon = (PolygonShape)fixture.getShape();

                    //Create a temporary vertex that will help setting the path.
                    //Set it to the first vertex position.
                    Vec2 tempVertex = Transform.mul(xForm, polygon.m_vertices[0]);

                    //Create the path and move to the first vertex coordinate.
                    Path polygonPath = new Path();
                    polygonPath.moveTo(tempVertex.x * gameWorld.ratio(),
                                       tempVertex.y * gameWorld.ratio());

                    //Create lines between every vertex coordinate.
                    for (int i = 0; i < polygon.getVertexCount(); i++)
                    {
                        tempVertex = Transform.mul(xForm, polygon.m_vertices[i]);

                        polygonPath.lineTo(tempVertex.x * gameWorld.ratio(),
                                           tempVertex.y * gameWorld.ratio());
                    }

                    //Go back to the first coordinate to close the loop.
                    polygonPath.close();

                    //Draw the polygon fill.
                    canvas.drawPath(polygonPath, paint);

                    //Draw the polygon stroke (edges).
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawPath(polygonPath, paint);
                }
            }
        }


        //Draw all the joints
        if (DRAW_JOINTS)
        {
            for (Joint joint = world.getJointList(); joint != null; joint = joint.getNext())
            {
                paint.setStyle(Paint.Style.STROKE);
                paint.setARGB(200, 255, 217, 102);

                Vec2 anchorA = new Vec2();
                joint.getAnchorA(anchorA);

                Vec2 anchorB = new Vec2();
                joint.getAnchorB(anchorB);

                Path jointPath = new Path();

                jointPath.moveTo(anchorA.x * gameWorld.ratio(),
                                 anchorA.y * gameWorld.ratio());

                jointPath.lineTo(anchorB.x * gameWorld.ratio(),
                                 anchorB.y * gameWorld.ratio());

                //Draw the joint as a line.
                canvas.drawPath(jointPath, paint);

                // TODO: Would it be necessary to differentiate the types of joints?
                // This was only tested with distance joints so far.
            }
        }


        //Draw the framerate on the screen.
        if (SHOW_FRAMERATE)
        {
            //The paint to output text.
            Paint textPaint = new Paint();
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(16);
            textPaint.setAntiAlias(true);
            textPaint.setTypeface(Typeface.MONOSPACE);

            canvas.drawText(new DecimalFormat("#.#").format(gameWorld.getFps()) , 10.0f, 20.0f, textPaint);
        }
    }
}
