package tutorialFX;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author dilip
 */
public class Limb {


    private static long s_IDMAX = 0;

    private final LimbTyp limbTyp;
    private final long id;
    //JavaFX UI for a limb
    public Node node;
    //limbs width and height in JBox2D world
    public float w;
    // box2d Model
    protected Body bodyd2;

    protected float h;
    private float liveEnergy = 120f;
    // age is counted down, until death: age <= 0
    private long age = 0;
    // the generation counter... because it is interesting
    private long generation;
    //X and Y position of the limb in JBox2D world
    private float posX;
    private float posY;
    private HashMap<Limb, Integer> touchingLimbs = new HashMap<Limb, Integer>();
    private float density;
    private float friction;
    private float restitution;


    /**
     * Create arbitrary limb.
     *
     * @param posX
     * @param posY
     * @param color
     * @param w
     * @param h
     */
    public Limb(float posX, float posY, float w, float h, LimbTyp color, long generation,
                float density, float friction, float restitution) {
        this.posX = posX;
        this.posY = posY;
        this.w = w;
        this.h = h;
        limbTyp = color;
        this.generation = generation;

        this.density = density;
        this.friction = friction;
        this.restitution = restitution;

        this.id = s_IDMAX++;

        this.age = Math.round(Utils.MAXAGE * ModifyByPercent(0.2f));
    }

    public Paint getFill() {
        final Color color;
        if (liveEnergy <= 0) color = Color.PURPLE;  // starved!
        else if (age <= 0) color = Color.GRAY;  // dead by age!
        else color = limbTyp.getColor();

        if (touchingLimbs.size() > 0) {
//            if (generation > 0) {
//                System.err.println(this + " touching = " + touchingLimbs);
//            }
            return color;
        } else {
            Paint paint = Utils.getGradient(color);
            return paint;
        }
    }

    public void updateEnergy(ArrayList<Limb> died, ArrayList<Limb> born) {

        final boolean debug = false && id % 100 == 0;
        StringBuilder sb = new StringBuilder();


        if (died.contains(this)) {
            return;
        }

        age -= 1;

        if (age <= 0) {
            // dead limbs are re-added
            final boolean add = died.add(this);
            if (add && debug)
                sb.append(": too old!\n ");
            return;
        }

        // sun radiation gives energy to green ones only!
        switch (limbTyp) {
            case EATER: // this is an eater!
                final Set<Limb> limbs = touchingLimbs.keySet();
                if (limbs.size() > 0)
                    for (Limb other : limbs) {
                        if (other.limbTyp == LimbTyp.ENERGY) { // only eater touching energy
                            // collision with other limb gives energy to this limb
                            float l = Utils.EAT_EFFICIENCY * Math.max(w, h);
                            liveEnergy += l;
                            // and removes from the other
                            other.removeEnergy(died, l);
                            if (debug) sb.append(": took ").append(l).append(" from ").append(other);
                        } else if (other.limbTyp == LimbTyp.EATER) {
                            // collision with other limb gives energy to this limb
                            float l = Utils.RED_HURTS * (float) Math.sqrt(w * w + h * h);
                            // hitting a red one hurts myself too
                            removeEnergy(died, l);
                            if (debug) sb.append(": lost ").append(l).append(" by attack of ").append(other);

                        }
                    }
                else {
                    // else the living of read is expensive
                    final float l = Utils.LIVINGCost * (float) Math.sqrt(w * w + h * h);
                    removeEnergy(died, l);
                    if (debug) sb.append(": lost ").append(l);
                }
                break;
            case ENERGY:
                final float l = Utils.SUNRADIATION * (float) Math.sqrt(w + h);
                liveEnergy += l;
                if (debug) sb.append(": won ").append(l);
                break;
        }


        if (liveEnergy > Utils.MinBIRTHENERGY) {
            if (debug) sb.append(" mom!");
            born.add(this);
            // in any case remove the energy as if the birth took place!
            liveEnergy = liveEnergy / 2f;  // giving birth costs a lot!
        }

        if (debug) System.err.println(this + sb.toString());

    }

    public Limb createChild() {
        final Limb limb = new Limb(Utils.r.nextFloat() * Utils.WIDTHd2,
                Utils.r.nextFloat() * Utils.HEIGHTd2,
                w * ModifyByPercent(0.1f),
                h * ModifyByPercent(0.1f),
                limbTyp,
                generation + 1,
                density * ModifyByPercent(0.1f),
                friction * ModifyByPercent(0.1f),
                restitution * ModifyByPercent(0.1f)
        );

        System.err.println("\n    Mother = " + this);
        System.err.println("child limb = " + limb);
        System.err.println("  w: " + w + "->" + limb.w);
        System.err.println("  h: " + h + "->" + limb.h);
        System.err.println("  A: " + h * w + "->" + limb.h * limb.w);
        // seems not relevant!
//            System.err.println("  f: " + friction + "->" + limb.friction);
//            System.err.println("  d: " + density + "->" + limb.density);
//            System.err.println("  r: " + restitution + "->" + limb.restitution);
        return limb;
    }

    /**
     * Random between [1-p,1+p]
     *
     * @param percent 0..1.0f
     * @return factor
     */
    private float ModifyByPercent(float percent) {
        return 1f + (Utils.r.nextFloat() - 0.5f) * percent;
    }

    private void removeEnergy(ArrayList<Limb> died, final float energy) {
        liveEnergy -= energy;
        if (liveEnergy <= 0) {
            if (age > 0) {
                age = 0;
            }
            died.add(this);
        }
    }


    @Override
    public String toString() {
        return limbTyp + "#" + id + ": gen=" + generation + " age=" + age + " energy=" + liveEnergy;
    }

    protected void createBodyAndNode() {
        if (bodyd2 == null)
            setupBody(this);
        if (node == null)
            node = createNode();
    }

    /**
     * This method creates a ball by using Circle object from JavaFX and CircleShape from JBox2D
     */
    private Node createNode() {

//        System.err.println("create Node for " + this);

        //Create an UI for ball - JavaFX code
        Rectangle fxBox = new Rectangle();

        fxBox.setHeight(Utils.toPixelHeight(h));
        fxBox.setWidth(Utils.toPixelWidth(w));
        fxBox.setFill(getFill()); //set look and feel


        fxBox.setCache(true); //Cache this object for better performance

        // we store the reference to this limb
        fxBox.setUserData(this);

        /**
         * Set ball position on JavaFX scene. We need to convert JBox2D coordinates
         * to JavaFX coordinates which are in pixels.
         */
        fxBox.setLayoutX(Utils.toPixelPosX(bodyd2.getPosition().x - w / 2f));
        fxBox.setLayoutY(Utils.toPixelPosY(bodyd2.getPosition().y + h / 2f));

        return fxBox;
    }

    static void setupBody(Limb limb) {
        assert limb.bodyd2 == null : "Don't setup body twice for " + limb;
//        System.err.println("setup body for " + this);
        //Create an JBox2D body definition for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(limb.posX, limb.posY);
        bd.setBullet(true);
        bd.allowSleep = true;

        PolygonShape cs = new PolygonShape();
        cs.setAsBox(limb.w / 2f, limb.h / 2f);

        // Create a fixture for limb
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = limb.density;
        fd.friction = limb.friction;
        fd.restitution = limb.restitution;

        limb.bodyd2 = Utils.world.createBody(bd);
        /**
         * Virtual invisible JBox2D body . Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        final Fixture fixture = limb.bodyd2.createFixture(fd);
        fixture.setUserData(limb);
    }

    public void startContact(Limb other) {

        if (other.isDead() || other == this) {
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
        if (other.isDead() || other == this) {
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

    public boolean isDead() {
        return age <= 0 || liveEnergy <= 0;
    }

    public long deadSince() {
        if (age < 0) return -age;
        else return 0;
    }


    enum LimbTyp {
        EATER(Color.RED, "R"),
        ENERGY(Color.GREEN, "G");

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
    }


}