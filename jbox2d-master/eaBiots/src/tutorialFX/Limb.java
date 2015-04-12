package tutorialFX;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author dilip
 */
public class Limb {


    private static final float EAT_EFFICIENCY = 0.6f;
    private static final long SUNRADIATION = 3;
    private static final long LIVINGCost = 1;
    private static final long MAXAGE = 8500;
    private static final int MinBIRTHENERGY = 6000;
    private static long s_IDMAX = 0;
    private final LimbTyp limbTyp;
    private final long id;
    //JavaFX UI for limb
    public Node node;
    protected Body bodyd2;
    Random r = new Random(System.currentTimeMillis());
    long liveEnergy = 120;
    long age = 0;
    long deadSince = -1;
    long generation;
    //X and Y position of the limb in JBox2D world
    private float posX;
    private float posY;
    //limbs width and height in JBox2D world
    private float w;
    private float h;
    private HashMap<Limb, Integer> touchingLimbs = new HashMap<Limb, Integer>();
    /**
     * Create a red ball
     *
     * @param w
     * @param h
     * @param posX
     * @param posY
     * @param typ
     */
    public Limb(float posX, float posY, final LimbTyp typ, long generation, final float w, final float h) {
        this(posX, posY, w, h, typ, generation);
    }

    /**
     * Create arbitrary ball.
     *
     * @param posX
     * @param posY
     * @param color
     */
    public Limb(float posX, float posY, float w, float h, LimbTyp color, long generation) {
        this.posX = posX;
        this.posY = posY;
        this.w = w;
        this.h = h;
//        this.gradient = Utils.getBallGradient(color.getColor());
        limbTyp = color;
        this.generation = generation;

        this.id = s_IDMAX++;
    }

    public Paint getFill() {
        Color color = limbTyp.getColor();
        if (liveEnergy <= 0 || deadSince > 0) color = Color.PURPLE;
        if (touchingLimbs.size() > 0) {
            return color;
        } else return Utils.getBallGradient(color);
    }


    public void updateEnergy(ArrayList<Limb> died, ArrayList<Limb> born) {
        age += 1;
        if (died.contains(this)) return;

        // sun radiation gives energy to green ones only!
        switch (limbTyp) {
            case EATER: // this is an eater!
                for (Limb other : touchingLimbs.keySet()) {
                    if (other.limbTyp == LimbTyp.ENERGY) { // only eater touching energy
                        float limbRation = w * h / other.w / other.h;
                        final long l = Math.max(0, Math.round(EAT_EFFICIENCY * limbRation * other.liveEnergy));
                        liveEnergy += l;
                        other.liveEnergy -= l;

                        if (other.liveEnergy <= 0) {
                            died.add(other);
                            other.deadSince = age;
                        }
                    }

                    liveEnergy -= LIVINGCost;
                    if (liveEnergy <= 0) {
                        died.add(this);
                        this.deadSince = age;
                    }

                }

                break;
            case ENERGY:
                liveEnergy += SUNRADIATION;
        }



//        if (age % 100 == 0) {
//            System.err.println(this);
//        }

        if (age > MAXAGE) {
            died.add(this);
            this.deadSince = age;
        }

        if (liveEnergy > MinBIRTHENERGY) {
            final Limb limb = new Limb(r.nextFloat() * Utils.WIDTHd2,
                    r.nextFloat() * Utils.HEIGHTd2,
                    limbTyp,
                    generation + 1,
                    w + r.nextFloat() - 0.5f,
                    h + r.nextFloat() - 0.5f
            );
            System.err.println("new born limb = " + limb);
            liveEnergy = liveEnergy / 2;  // giving birth costs a lot!
            System.err.println("       Mother = " + this);
            born.add(limb);
        }

    }

    @Override
    public String toString() {
        return ("Limb#" + id + ": gen=" + generation + " age=" + age + " energy=" + liveEnergy);
    }

    protected void createBodyAndNode() {
        setupBody();
        node = createNode();
    }

    /**
     * This method creates a ball by using Circle object from JavaFX and CircleShape from JBox2D
     */
    private Node createNode() {

        System.err.println("create Node for " + this);

        //Create an UI for ball - JavaFX code
        Rectangle fxBox = new Rectangle();

        fxBox.setHeight(Utils.toPixelHeight(h));
        fxBox.setWidth(Utils.toPixelWidth(w));
        fxBox.setFill(getFill()); //set look and feel


        fxBox.setCache(true); //Cache this object for better performance


        fxBox.setUserData(bodyd2);

        /**
         * Set ball position on JavaFX scene. We need to convert JBox2D coordinates
         * to JavaFX coordinates which are in pixels.
         */
        fxBox.setLayoutX(Utils.toPixelPosX(bodyd2.getPosition().x - w));
        fxBox.setLayoutY(Utils.toPixelPosY(bodyd2.getPosition().y + h));

        return fxBox;
    }

    private void setupBody() {
        System.err.println("setup body for " + this);
        //Create an JBox2D body definition for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(posX, posY);
        bd.setBullet(false);
        bd.allowSleep = true;

        PolygonShape cs = new PolygonShape();
        cs.setAsBox(w / 2f, h / 2f);
//        cs.m_radius = radius * 0.1f;  //We need to convert radius to JBox2D equivalent

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 0.9f;
        fd.friction = 0.3f;
        fd.restitution = 0.8f;

        bodyd2 = Utils.world.createBody(bd);
        /**
         * Virtual invisible JBox2D body . Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        final Fixture fixture = bodyd2.createFixture(fd);
        fixture.setUserData(this);

    }

    public void startContact(Limb other) {
        Integer counter = touchingLimbs.get(other);
        if (counter == null) {
            counter = 1;
        } else {
            counter = counter + 1;
        }
        touchingLimbs.put(other, counter);
    }

    public void endContact(Limb other) {
        Integer counter = touchingLimbs.get(other);
        if (counter == null) {
            counter = 0;
        } else {
            counter = counter - 1;
        }
        if (counter <= 0) touchingLimbs.remove(other);
    }


    enum LimbTyp {
        EATER(Color.RED),
        ENERGY(Color.GREEN);

        private Color color;

        LimbTyp(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }


}