package com.daedalusgames.shotgun_blob;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

/**
 * // -------------------------------------------------------------------------
/**
 *  Temporary level class to build the boundary edges.
 *
 *  @author Artur
 *  @version Oct 1, 2012
 */
public class Level
{
    private Body ground;
    private Body leftWall;
    private Body rightWall;
    private Body roof;

    /**
     * Level constructor.
     */
    public Level()
    {
        //Ground
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position = new Vec2((Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, Main.displayMetrics.heightPixels / Main.RATIO );
        ground = Main.getWorld().createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox( (Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, (10.0f / 2.0f) / Main.RATIO );
        //shape.setAsEdge(new Vec2(-(Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, 0.0f), new Vec2((Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, 0.0f));
        ground.createFixture(shape, 0.0f);


        //Right Wall
        bd.position = new Vec2(Main.displayMetrics.widthPixels / Main.RATIO, (Main.displayMetrics.heightPixels / 2.0f) / Main.RATIO);
        rightWall = Main.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / Main.RATIO, (Main.displayMetrics.heightPixels / 2.0f) / Main.RATIO );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        rightWall.createFixture(shape, 0.0f);


        //Left Wall
        bd.position = new Vec2(0.0f, (Main.displayMetrics.heightPixels / 2) / Main.RATIO);
        leftWall = Main.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / Main.RATIO, (Main.displayMetrics.heightPixels / 2.0f) / Main.RATIO );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        leftWall.createFixture(shape, 0.0f);


        //Roof
        bd.position = new Vec2((Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, 0.0f );
        roof = Main.getWorld().createBody(bd);
        shape.setAsBox( (Main.displayMetrics.widthPixels / 2.0f) / Main.RATIO, (10.0f / 2.0f) / Main.RATIO );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        roof.createFixture(shape, 0.0f);

    }

}
