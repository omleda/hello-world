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


    private static long s_IDMAX = 0;

    private static final float EAT_EFFICIENCY = 0.07f;
    private static final float SUNRADIATION = 21f / 25f;
    private static final long LIVINGCost = 1;
    private static final long MAXAGE = 8500;
    private static final int MinBIRTHENERGY = 6000;

    private final LimbTyp limbTyp;
    private final long id;
    //JavaFX UI for a limb
    public Node node;
    // box2d Model
    protected Body bodyd2;

    private Random r = new Random(System.currentTimeMillis());
    private long liveEnergy = 120;
    private long age = 0;
    private long deadSince = -1;
    private long generation;
    //X and Y position of the limb in JBox2D world
    private float posX;
    private float posY;
    //limbs width and height in JBox2D world
    public float w;
    protected float h;
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
    }

    public Paint getFill() {
        Color color = limbTyp.getColor();
        if (liveEnergy <= 0 || deadSince > 0) color = Color.PURPLE;
        if (touchingLimbs.size() > 0) {
//            if (generation > 0) {
//                System.err.println(this + " touching = " + touchingLimbs);
//            }
            return color;
        } else return Utils.getBallGradient(color);
    }

    private Limb kill() {
        if (isDead()) return this;
        deadSince = age;
        return this;
    }


    public void updateEnergy(ArrayList<Limb> died, ArrayList<Limb> born) {
        age += 1;

        if (died.contains(this)) {
            return;
        }
        if (isDead()) {
            died.add(this);
        }

        // sun radiation gives energy to green ones only!
        switch (limbTyp) {
            case EATER: // this is an eater!
                for (Limb other : touchingLimbs.keySet()) {
                    if (other.limbTyp == LimbTyp.ENERGY) { // only eater touching energy
                        float limbRation = Math.max(w, h) / Math.max(other.w, other.h);
                        final long l = Math.max(0, Math.round(EAT_EFFICIENCY * limbRation * other.liveEnergy));
                        liveEnergy += l;
                        other.removeEnergy(died, l);
                    }
                    removeEnergy(died, LIVINGCost * Math.sqrt(w * h));
                }

                break;
            case ENERGY:
                liveEnergy += SUNRADIATION * w * h;
        }

        if (age > MAXAGE) {
            // purple limbs are re-added
            boolean alreadyDead = isDead();
            final boolean add = died.add(this.kill());
            if (add && !alreadyDead)
                System.err.println("too old: " + this);
            return;
        }

        if (liveEnergy > MinBIRTHENERGY) {

            born.add(this);
            // in any case remove the energy as if the birth took place!
            liveEnergy = liveEnergy / 2;  // giving birth costs a lot!


        }

    }

    public Limb createChild() {
        final Limb limb = new Limb(r.nextFloat() * Utils.WIDTHd2,
                r.nextFloat() * Utils.HEIGHTd2,
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

    private float ModifyByPercent(float percent) {
        return 1f + (r.nextFloat() - 0.5f) * percent;
    }

    private void removeEnergy(ArrayList<Limb> died, double livingCost) {
        liveEnergy -= Math.round(livingCost);
        if (liveEnergy <= 0) {
            died.add(this.kill());
        }
    }


    @Override
    public String toString() {
        return limbTyp + "#" + id + ": gen=" + generation + " age=" + age + " energy=" + liveEnergy;
    }

    protected void createBodyAndNode() {
        setupBody();
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


        //TODO:  shouldn't we set the limb ??
        fxBox.setUserData(bodyd2);

        /**
         * Set ball position on JavaFX scene. We need to convert JBox2D coordinates
         * to JavaFX coordinates which are in pixels.
         */
        fxBox.setLayoutX(Utils.toPixelPosX(bodyd2.getPosition().x - w / 2f));
        fxBox.setLayoutY(Utils.toPixelPosY(bodyd2.getPosition().y + h / 2f));

        return fxBox;
    }

    private void setupBody() {
//        System.err.println("setup body for " + this);
        //Create an JBox2D body definition for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(posX, posY);
        bd.setBullet(true);
        bd.allowSleep = true;

        PolygonShape cs = new PolygonShape();
        cs.setAsBox(w / 2f, h / 2f);

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;

        bodyd2 = Utils.world.createBody(bd);
        /**
         * Virtual invisible JBox2D body . Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        final Fixture fixture = bodyd2.createFixture(fd);
        fixture.setUserData(this);

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
        return deadSince > 0;
    }

    public boolean isDeadSince(int i) {
        return age - deadSince > i;
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