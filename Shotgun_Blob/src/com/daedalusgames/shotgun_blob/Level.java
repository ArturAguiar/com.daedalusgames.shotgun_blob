package com.daedalusgames.shotgun_blob;

import org.jbox2d.dynamics.Fixture;
import java.util.HashSet;
import android.graphics.RectF;
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

    private HashSet<Fixture> levelFixtures;

    private RectF boundingBox;

    /**
     * Level constructor.
     * @param myGameWorld The game world.
     */
    public Level(GameWorld myGameWorld)
    {
        gameWorld = myGameWorld;

        levelFixtures = new HashSet<Fixture>();

        //Ground
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position = new Vec2(gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), gameWorld.getDisplayMetrics().heightPixels / gameWorld.ratio() );
        ground = gameWorld.getWorld().createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox( gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), (100.0f / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(-(gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), 0.0f), new Vec2((gameWorld.getDisplayMetrics().widthPixels / 2.0f) / gameWorld.ratio(), 0.0f));
        levelFixtures.add(ground.createFixture(shape, 0.0f));


        //Right Wall
        bd.position = new Vec2(2.0f * gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), 0.0f);
        rightWall = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / gameWorld.ratio(), gameWorld.getDisplayMetrics().heightPixels / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        levelFixtures.add(rightWall.createFixture(shape, 0.0f));


        //Left Wall
        bd.position = new Vec2(0.0f, 0.0f);
        leftWall = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( (10.0f / 2.0f) / gameWorld.ratio(), gameWorld.getDisplayMetrics().heightPixels / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        levelFixtures.add(leftWall.createFixture(shape, 0.0f));


        //Roof
        bd.position = new Vec2(gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), -gameWorld.getDisplayMetrics().heightPixels / gameWorld.ratio() );
        roof = gameWorld.getWorld().createBody(bd);
        shape.setAsBox( gameWorld.getDisplayMetrics().widthPixels / gameWorld.ratio(), (10.0f / 2.0f) / gameWorld.ratio() );
        //shape.setAsEdge(new Vec2(0.0f, -40.0f), new Vec2(0.0f, 1000.0f));
        levelFixtures.add(roof.createFixture(shape, 0.0f));


        // Calculate the bounding box.
        boundingBox = this.calculateBoundingBox();
    }

    /**
     * Calculates the bounding box based on the fixtures.
     */
    private RectF calculateBoundingBox()
    {
        RectF box = null;

        for (Fixture fixt : levelFixtures)
        {
            Vec2 bodyPosition = fixt.getBody().getPosition();

            if ( box == null )
            {
                box = new RectF(bodyPosition.x, bodyPosition.y,
                                bodyPosition.x, bodyPosition.y);
            }
            else
            {
                if ( bodyPosition.x < box.left )
                    box.left = bodyPosition.x;

                else if ( bodyPosition.x > box.right )
                    box.right = bodyPosition.x;


                if ( bodyPosition.y < box.top )
                    box.top = bodyPosition.y;

                else if ( bodyPosition.y > box.bottom )
                    box.bottom = bodyPosition.y;
            }
        }

        return box;
    }

    /**
     * Returns this level's bounding box.
     * @return The bounding box of this level.
     */
    public RectF getBoundingBox()
    {
        return boundingBox;
    }

}
