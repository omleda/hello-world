package tutorialFX;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author dilip
 */
public class Limb {

    //JavaFX UI for limb
    public Node node;

    //X and Y position of the limb in JBox2D world
    private float posX;
    private float posY;

    //limbs width and height in JBox2D world
    private float w;
    private float h;

    /**
     * There are three types bodies in JBox2D – Static, Kinematic and dynamic
     * In this application static bodies (BodyType.STATIC – non movable bodies)
     * are used for drawing hurdles and dynamic bodies (BodyType.DYNAMIC–movable bodies)
     * are used for falling balls
     */
    private BodyType bodyType;

    //Gradient effects for balls
    private LinearGradient gradient;

    /**
     * Create a red ball
     *
     * @param posX
     * @param posY
     */
    public Limb(float posX, float posY) {
        this(posX, posY, Utils.BALL_SIZE,Utils.BALL_SIZE, BodyType.DYNAMIC, Color.RED);
    }

    /**
     * Create arbitrary ball.
     *
     * @param posX
     * @param posY
     * @param radius
     * @param bodyType
     * @param color
     */
    public Limb(float posX, float posY, float w, float h, BodyType bodyType, Color color) {
        this.posX = posX;
        this.posY = posY;
        this.w = w;
        this.h = h;
        this.bodyType = bodyType;
        this.gradient = Utils.getBallGradient(color);
        node = create();
    }

    /**
     * This method creates a ball by using Circle object from JavaFX and CircleShape from JBox2D
     */
    private Node create() {
        //Create an UI for ball - JavaFX code
        Rectangle ball = new Rectangle();

        ball.setHeight(Utils.toPixelHeight(h));
        ball.setWidth(Utils.toPixelWidth(w));
        ball.setFill(gradient); //set look and feel

        /**
         * Set ball position on JavaFX scene. We need to convert JBox2D coordinates
         * to JavaFX coordinates which are in pixels.
         */
        ball.setLayoutX(Utils.toPixelPosX(posX));
        ball.setLayoutY(Utils.toPixelPosY(posY));

        ball.setCache(true); //Cache this object for better performance

        //Create an JBox2D body defination for ball.
        BodyDef bd = new BodyDef();
        bd.type = bodyType;
        bd.position.set(posX, posY);

        PolygonShape cs = new PolygonShape();
        cs.setAsBox(h*0.1f,w*0.1f);
//        cs.m_radius = radius * 0.1f;  //We need to convert radius to JBox2D equivalent

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 0.9f;
        fd.friction = 0.3f;
        fd.restitution = 0.6f;

        /**
         * Virtual invisible JBox2D body of ball. Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        Body body = Utils.world.createBody(bd);
        body.createFixture(fd);
        ball.setUserData(body);
        return ball;
    }
}