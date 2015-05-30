package tutorialFX;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.HashMap;
import java.util.Set;

/**
 * @author dilip
 */
public class Limb {


    private final LimbTyp limbTyp;
    private final float initAngle;
    //JavaFX UI for a limb
    public Node node;
    //limbs width and height in JBox2D world
    public float w;

    // box2d Model
    protected Body bodyd2;
    protected float h;
    //X and Y position of the limb in JBox2D world
    private float posX;
    private float posY;
    private float density;
    private float friction;
    private float restitution;
    private HashMap<Limb, Integer> touchingLimbs = new HashMap<Limb, Integer>();

    // to which this limb belongs to.
    Biot biot;


    /**
     * Create arbitrary limb.
     *
     * @param posX
     * @param posY
     * @param color
     * @param w
     * @param h
     */
    public Limb(float posX, float posY, float w, float h, LimbTyp color,
                float density, float friction, float restitution, float initialAngle,
                Biot biot) {
        this.posX = posX;
        this.posY = posY;
        this.w = w;
        this.h = h;
        limbTyp = color;

        this.density = density;
        this.friction = friction;
        this.restitution = restitution;

        this.initAngle = initialAngle;
        this.biot = biot;
    }

    void setupBody() {
        assert bodyd2 == null : "Don't setup body twice for " + this;
//        System.err.println("setup body for " + this);
        //Create an JBox2D body definition for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.allowSleep = true;
        bd.position.set(posX, posY);
        bd.setBullet(true);

        // setting viscosity
        //        bd.setAngularDamping();
        // bd.setLinearDamping();

        PolygonShape cs = new PolygonShape();
//        cs.setAsBox(w / 2f, h / 2f);
        cs.setAsBox(w / 2f, h / 2f, new Vec2(0, 0), initAngle);
        FixtureDef fd = getFixtureDef(this);
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;

        fd.shape = cs;

        bodyd2 = Utils.world.createBody(bd);

        /**
         * Virtual invisible JBox2D body . Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        final Fixture fixture = bodyd2.createFixture(fd);
        fixture.setUserData(this);
    }

    public Paint getFill() {
        final Color color;
        if (biot.energy <= 0) color = Color.PURPLE;  // starved!
        else if (biot.age <= 0) color = Color.GRAY;  // dead by age!
        else color = limbTyp.getColor();

        if (touchingLimbs.size() > 0) {
//            if (generation > 0) {
//                System.err.println(this + " touching = " + touchingLimbs);
//            }
            return color;
        } else {
            return Utils.getGradient(color);
        }
    }

    public float updateEnergy(boolean debug, StringBuilder sb) {
        float deltaEnergy = 0;

// once per limb!
        switch (limbTyp) {
            case RED: {// this is an red limb!
                // the living of red is expensive
                final float v = -Utils.LIVINGCostOfRedLimb * (float) Math.sqrt(w * w + h * h);
                deltaEnergy = v;
//                    removeEnergy(died, l);
                if (debug) sb.append("\n  red looses ").append(v);
                break;
            }
            case GREEN: { // this is green
                final float v = Utils.SUNRADIATION * (float) Math.sqrt(w * w + h * h);
                deltaEnergy = v;
                if (debug) sb.append("\n  green wins ").append(v);
                break;
            }
            default:
                System.err.println("unhandled limb type   " + limbTyp);
                deltaEnergy = 0;
        }


        // Energy win or loss due to collisions
        final Set<Limb> limbs = touchingLimbs.keySet();
        if (debug && !limbs.isEmpty()) {
            sb.append("\n   collision results:");
        }
        for (Limb other : limbs) {
            switch (limbTyp) {
                case RED: {// this is an eater!
                    if (other.limbTyp == LimbTyp.GREEN) { // only eater touching energy
                        // collision with other limb gives energy to this limb
                        float l = Utils.EAT_EFFICIENCY * Math.max(w, h); // take acc to red size.
                        deltaEnergy += l;
                        // and removes from the other
//                            other.removeEnergy(died, l);
                        if (debug) sb.append("\n : took ").append(l).append(" from ").append(other.biot.id);
                    } else if (other.limbTyp == LimbTyp.RED) {
                        // collision with other limb gives energy to this limb
                        // symmetrical hurt.
                        float l = -Utils.RED_HURTS_RED * Math.max(other.w, other.h);
                        deltaEnergy += l;

                        // hitting a red one hurts myself too
//                            removeEnergy(died, l);
                        if (debug) sb.append("\n : hurt ").append(l).append(" by ").append(other.biot.id);
                    }
                    break;
                }
                case GREEN: {
                    if (other.limbTyp == LimbTyp.RED) { // only eater touching energy
                        float l = -Utils.EAT_EFFICIENCY * Math.max(other.w, other.h); // give according to red size
                        deltaEnergy += l;
                        if (debug) sb.append("\n : lost ").append(l).append(" by attack of ").append(other.biot.id);
                    } else if (other.limbTyp == LimbTyp.GREEN) {
                        // green just bounces on green
                        if (debug) sb.append("\n : touched other green ").append(other.biot.id);
                    }
                    break;
                }
                default:
                    System.err.println("unhandled limb type   " + limbTyp);
                    deltaEnergy = 0;
            }
        }
        return deltaEnergy;
    }


    public Limb createSimilar(Biot biot, Transform offset) {
        final Limb limb = new Limb(offset.p.x, offset.p.y,
                w * Utils.RandomInPercentageRange(0.2f),
                h * Utils.RandomInPercentageRange(0.2f),
                (Utils.RandomBoolean(0.005f) ? limbTyp.other() : limbTyp),
                density * Utils.RandomInPercentageRange(0.2f),
                friction * Utils.RandomInPercentageRange(0.2f),
                restitution * Utils.RandomInPercentageRange(0.2f),
                offset.q.getAngle(),
                biot
        );

//        System.err.println("\n    Mother = " + this);
//        System.err.println("child limb = " + limb);
//        System.err.println("  w: " + w + "->" + limb.w);
//        System.err.println("  h: " + h + "->" + limb.h);
//        System.err.println("  A: " + h * w + "->" + limb.h * limb.w);
        // seems not relevant!
//            System.err.println("  f: " + friction + "->" + limb.friction);
//            System.err.println("  d: " + density + "->" + limb.density);
//            System.err.println("  r: " + restitution + "->" + limb.restitution);
        return limb;
    }


    protected void createBodyAndNode(Group root) {
        if (bodyd2 == null)
            setupBody();
        if (node == null) {
            createNode(root);
        }
    }

    /**
     * This method creates a ball by using Circle object from JavaFX and CircleShape from JBox2D
     */
    private void createNode(Group root) {
        assert node == null : "Don't setup node twice for " + this;
//        System.err.println("create Node for " + this);

        //Create an UI for ball - JavaFX code
        Rectangle fxBox = new Rectangle();

        fxBox.setHeight(Utils.toPixelHeight(h));
        fxBox.setWidth(Utils.toPixelWidth(w));
        fxBox.setFill(getFill()); //set look and feel


        fxBox.setCache(true); //Cache this object for better performance

        // we store the reference to this limb
        fxBox.setUserData(this);
        node = fxBox;

        /**
         * Set ball position on JavaFX scene. We need to convert JBox2D coordinates
         * to JavaFX coordinates which are in pixels.
         */
        final Vec2 position = bodyd2.getPosition();
        node.setLayoutX(Utils.toPixelPosX(position.x - w / 2f));
        node.setLayoutY(Utils.toPixelPosY(position.y + h / 2f));
        node.setRotate(-Math.toDegrees(bodyd2.getAngle() + initAngle));

        root.getChildren().add(node);
    }

    public void destroy(Group root) {
        Utils.world.destroyBody(bodyd2);
        root.getChildren().remove(node);
    }


    private static FixtureDef getFixtureDef(Limb limb) {
        // Create a fixture for limb
        FixtureDef fd = new FixtureDef();
        fd.density = limb.density;
        fd.friction = limb.friction;
        fd.restitution = limb.restitution;

//        ((PolygonShape) fd.getShape()).setAsBox();
        return fd;
    }

    public void startContact(Limb other) {

        if (other.biot.isDead() || other == this) {
            touchingLimbs.remove(other);
        } else {
            Integer counter = touchingLimbs.get(other);
            if (counter == null) {
                counter = 1;
            } else {
                counter = counter + 1;
            }
            touchingLimbs.put(other, counter);
        }
    }

    public void endContact(Limb other) {
        if (other.biot.isDead() || other == this) {
            touchingLimbs.remove(other);
        } else {
            Integer counter = touchingLimbs.get(other);
            if (counter == null) {
                counter = 0;
            } else {
                counter = counter - 1;
            }
            if (counter <= 0) touchingLimbs.remove(other);
        }
    }


    void drawLimb() {
        node.setLayoutX(Utils.toPixelPosX(bodyd2.getPosition().x - w / 2f));
        node.setLayoutY(Utils.toPixelPosY(bodyd2.getPosition().y + h / 2f));
        node.setRotate(-Math.toDegrees(bodyd2.getAngle() + initAngle));
        ((Rectangle) node).setFill(getFill());
    }

    void combine(AABB aabb) {
        Fixture fixture = bodyd2.getFixtureList();
        final Transform transform = bodyd2.getTransform();

        while (fixture != null) {
            final Shape shape = fixture.getShape();
            final int childCount = shape.getChildCount();
            for (int child = 0; child < childCount; child++) {
                AABB shapeAABB = new AABB();
                shape.computeAABB(shapeAABB, transform, child);
                final float radius = shape.getRadius();
                aabb.upperBound.x -= radius;
                aabb.upperBound.y -= radius;
                aabb.lowerBound.x += radius;
                aabb.lowerBound.y += radius;
                aabb.combine(shapeAABB);
            }
            fixture = fixture.getNext();
        }
    }


    enum LimbTyp {
        RED(Color.RED, "R"),
        GREEN(Color.GREEN, "G");

        private final String s;
        private Color color;

        LimbTyp(Color color, String s) {
            this.color = color;
            this.s = s;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            return s;
        }

        public LimbTyp other() {
            if (this == RED) return GREEN;
            else return RED;
        }
    }


}