package tutorialFX;

import javafx.scene.paint.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.HashMap;
import java.util.Random;

/**
 * @author dilip then ea
 */
public class Utils {

    private static final Vec2 gravity = new Vec2(0, -1.0f);

    private static final float mutationRate = 2f;


    //Create a JBox2D world.
    // with gravity vector.
    public static final World world = new World(gravity);

    //Screen width and height  in pixels
    public static final int WIDTH_px = 500;
    public static final int HEIGHT_px = 500;

    public static final float WIDTHd2 = 500f;
    public static final float HEIGHTd2 = 500f;

    // step frequency
    public static final float DT = 1f / 25f;


    //Initial size in pixel
    public static final float LIMB_SIZE = 5f;

    //Total number of limbs
    public final static int NO_OF_INITIAL_BIOTS = 1;
    public static final int MAX_BIOTS = 150;


    ///////// interaction constants
    static final float InitialBIRTHENERGY = 3000f;

    static final long MAXAGE = 8500;  // 8500*DT / 60 = 5.6 Minutes of life time max

    static final float EAT_EFFICIENCY = 10.6f;

    static final float RED_HURTS_RED = 2.8f;

    static final float SUNRADIATION = 1.2f;

    static final float LIVINGCostOfRedLimb = 1.19f; // 0.04f;

    // the random generator
    static Random r = new Random(System.currentTimeMillis());

    static private final HashMap<Color, Paint> cachedPaints = new HashMap<>();


    public static void fourWalls() {
        // vier wände:
        // the Body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.setPosition(new Vec2());
        Body staticBody = world.createBody(bd);

        FixtureDef fd = new FixtureDef();
        //  parameter die bei allen Fixtures gelten
        fd.setRestitution(2.3f);


        // dann die shapes
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(WIDTHd2 / 2, 1f, new Vec2(WIDTHd2 / 2, 0), 0f);  // BODEN
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

        polygonShape.setAsBox(WIDTHd2 / 2, 1f, new Vec2(WIDTHd2 / 2, HEIGHTd2), 0f);  // Decke
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

        polygonShape.setAsBox(1f, HEIGHTd2 / 2, new Vec2(0f, HEIGHTd2 / 2), 0f);  // Linke Wand
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body


        polygonShape.setAsBox(1f, HEIGHTd2 / 2, new Vec2(WIDTHd2, HEIGHTd2 / 2), 0f);  // rechte Wand
        fd.setShape(polygonShape); // zur FD
        staticBody.createFixture(fd); // diese fixture muss zum Body

    }

    public static Paint getGradient(Color color) {
        Paint paint = cachedPaints.get(color);
        if (paint == null) {
            //This gives a gradient
            paint = new LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, color));
            cachedPaints.put(color, paint);
        }
        return paint;
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

    /**
     * Random between [1-p,1+p]  p multiplied by mutationRate
     *
     * @param percent 0..1.0f
     * @return factor
     */
    static float RandomInPercentageRange(float percent) {
        return 1f + (r.nextFloat() - 0.5f) * percent * mutationRate;
    }

    /**
     * Random between [min-max]. range stretched around mid by mutationRate
     *
     * @param min
     * @param max
     * @return random float
     */
    static float RandomInRange(float min, float max) {
        assert min < max;
        final float v = max - min;
        return min + v / 2f * (1 - mutationRate) + r.nextFloat() * mutationRate * v;
    }


    /**
     * Random between [min-max].  no stretch of range by mutationRate.
     *
     * @param min
     * @param max
     * @return random float
     */
    static float RandomInRangeNoMRATE(float min, float max) {
        assert min < max;
        return min + r.nextFloat() * (max - min);
    }

    /**
     * Vectore of length mutationRate in random direction.
     * @return
     */
    static Vec2 RandomUnitVector() {
        final Rot rot = new Rot(RandomInRange(0f, 360f * MathUtils.DEG2RAD));
        Vec2 result = new Vec2();
        rot.getXAxis(result);
        result.mulLocal(mutationRate);
        return result;
    }

    /**
     * return true with specified probability (approx) probablity is mulitplied by mutationRate.
     *
     * @param probability
     * @return true with the given probability
     */
    static boolean RandomBoolean(float probability) {
        return r.nextFloat() <= probability * mutationRate;
    }

}