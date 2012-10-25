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
    private GameWorld gameWorld;

    private Body ground;
    private Body leftWall;
    private Body rightWall;
    private Body roof;

    /**
     * Level constructor.
     * @param myGameWorld The game world.
     */
    public Level(GameWorld myGameWorld)
    {
        gameWorld = myGameWorld;

        //Ground
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position = new Vec2((gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), gameWorld.getDisplayMetrics().heightPixels / gameWorld.ratio() );
        ground = gameWorld.getWorld().createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox( (gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), (10.0f / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(-(gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), 0.0f), new Vec2((gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), 0.0f));
        ground.createFixture(shape, 0.0f);


        //Right Wall
        bd.position = new Vec2(gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), (gameWorld.getDisplayMetrics().heightPixels / 2.0f) / gameWorld.ratio());
        rightWall = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / gameWorld.ratio(), (gameWorld.getDisplayMetrics().heightPixels / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        rightWall.createFixture(shape, 0.0f);


        //Left Wall
        bd.position = new Vec2(0.0f, (gameWorld.getDisplayMetrics().heightPixels / 2) / gameWorld.ratio());
        leftWall = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / gameWorld.ratio(), (gameWorld.getDisplayMetrics().heightPixels / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        leftWall.createFixture(shape, 0.0f);


        //Roof
        bd.position = new Vec2((gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), 0.0f );
        roof = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( (gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), (10.0f / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        roof.createFixture(shape, 0.0f);

    }

}
