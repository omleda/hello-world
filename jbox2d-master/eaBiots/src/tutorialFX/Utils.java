package tutorialFX;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * @author dilip
 */
public class Utils {
    //Create a JBox2D world.
    // with gravity vector.
    public static final World world = new World(new Vec2(-10f, -10.0f));

    //Screen width and height  in pixels
    public static final int WIDTH_px = 500;
    public static final int HEIGHT_px = 500;

    public static final float WIDTHd2 = 100f;
    public static final float HEIGHTd2 = 100f;


    //Ball radius in pixel
    public static final float LIMB_SIZE = 5f;

    //Total number of balls
    public final static int NO_OF_BALLS = 10;

    //Ball gradient
    private final static LinearGradient LIMB_GRADIENT = new LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.RED));

    //This method adds a ground to the screen.
    public static void addGround(float width, float height) {
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(width, height);

        FixtureDef fd = new FixtureDef();
        fd.shape = ps;

        BodyDef bd = new BodyDef();

        bd.position = new Vec2(0.0f, -10f);

        world.createBody(bd).createFixture(fd);
    }


    public static void fourWalls() {
        // vier wände:
        // the Body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.setPosition(new Vec2());
        Body staticBody = world.createBody(bd);

        FixtureDef fd = new FixtureDef();
        //  parameter die bei allen Fixtures gelten
        fd.setRestitution(1.2f);


        // dann die shapes
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(WIDTHd2/2, 1f, new Vec2(WIDTHd2 / 2, 0), 0f);  // BODEN
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

        polygonShape.setAsBox(WIDTHd2/2, 1f, new Vec2(WIDTHd2 / 2, HEIGHTd2), 0f);  // Decke
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

        polygonShape.setAsBox(1f, HEIGHTd2/2, new Vec2(0f, HEIGHTd2/2), 0f);  // Linke Wand
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body


        polygonShape.setAsBox(1f, HEIGHTd2 / 2, new Vec2(WIDTHd2  , HEIGHTd2 / 2), 0f);  // rechte Wand
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

    }


    //This method creates a walls.
    public static void addWall(float posX, float posY, float width, float height) {
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(width, height);

        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 1.0f;
        fd.friction = 0.3f;

        BodyDef bd = new BodyDef();
        bd.position.set(posX, posY);

        Utils.world.createBody(bd).createFixture(fd);
    }

    //This gives a look and feel to balls
    public static LinearGradient getBallGradient(Color color) {
//        if (color.equals(Color.RED))
//            return LIMB_GRADIENT;
//        else
            return new LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, color));
    }

    //Convert a JBox2D x coordinate to a JavaFX pixel x coordinate
    public static double toPixelPosX(float posX) {
        return WIDTH_px * posX / WIDTHd2;
    }

    //Convert a JavaFX pixel x coordinate to a JBox2D x coordinate
    public static float toPosX(double posX) {
        return (float) (posX * WIDTHd2 / WIDTH_px);
    }

    //Convert a JBox2D y coordinate to a JavaFX pixel y coordinate
    public static double toPixelPosY(float posY) {
        return HEIGHT_px - posY * HEIGHT_px / HEIGHTd2;
    }

    //Convert a JavaFX pixel y coordinate to a JBox2D y coordinate
    public static float toPosY(double posY) {
        return (float) (HEIGHTd2 - posY * HEIGHTd2 / HEIGHT_px);
    }

    //Convert a JBox2D width to pixel width
    public static double toPixelWidth(float width) {
        return width * WIDTH_px / WIDTHd2;
    }

    //Convert a JBox2D height to pixel height
    public static double toPixelHeight(float height) {
        return height * HEIGHT_px / HEIGHTd2;
    }

    //Convert a pixel distance into JBox2D distance (in X axis)
    public static float toDistance(double radius) {
        return (float) (radius * WIDTHd2 / WIDTH_px);
    }
}