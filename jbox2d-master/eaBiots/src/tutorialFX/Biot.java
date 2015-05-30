package tutorialFX;

import javafx.scene.Group;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import java.util.*;

/**
 * Created by ea on 23.04.15.
 * <p>
 * A Biot consists of {@link Limb}, they are connected by {@link RevoluteJoint}s
 */
public class Biot {
    // book-keeping of the biots
    // top id for all biots.
    private static long s_IDMAX = 0;

    // ID for identifying each biot... not really used except for debugging and logging and such.
    protected final long id;
    // the generation counter... because it is interesting
    private long generation;


    // evolvable parameters: the blue print the genome...
    long maxAge = Utils.MAXAGE;

    // construction of Biot
    //  currently the limb n+1 is connected to limb n

    int NumbOfLimbs = 2;
    final ArrayList<Limb> limbs = new ArrayList<>(NumbOfLimbs);

    // offset between limbN-1 and LimbN
    final ArrayList<Transform> limbOffset = new ArrayList<>(NumbOfLimbs - 1);

    // Live states of the biots
    // the life energy of the biot.
    float energy;

    // age is counted down, until death: age <= 0
    long age;
    private float birthEnergy;

    // joint between limbN-1 and LimbN
    // the current operation state of the joints, and also the matching def
    final LinkedHashMap<RevoluteJoint, FlappingJointDef> limbJoints = new LinkedHashMap<>(NumbOfLimbs - 1);


    public Biot(long generation, float xPos, float yPos) {

        this.id = s_IDMAX++;
        this.generation = generation;

        this.age = maxAge;

        final Limb limb1 = new Limb(xPos, //Utils.WIDTHd2 / 2 + (1 + Utils.LIMB_SIZE) * 0,  // Xpos
                yPos, // Utils.HEIGHTd2 / 2 + (1 + Utils.LIMB_SIZE) * 0, // YPos
                Utils.LIMB_SIZE / 4, // width
                Utils.LIMB_SIZE * 4, // height
                Limb.LimbTyp.GREEN,
                // values of initial generation
                0.9f,// density
                0.3f, // friction
                0.8f,// restitution
                0.0f,// angle
                this
        );
        limb1.setupBody();

        // Offset and rotation of limb2 relative to Limb1
        Transform tr = new Transform(
                new Vec2(Utils.r.nextFloat() * Utils.LIMB_SIZE,
                        Utils.r.nextFloat() * Utils.LIMB_SIZE),
                new Rot((Utils.r.nextFloat() * 360 - 180) * MathUtils.DEG2RAD));

        limbOffset.add(tr);

        final Limb limb2 = new Limb(xPos + tr.p.x,  // Xpos
                yPos + tr.p.y,
                Utils.LIMB_SIZE * 3, // width
                Utils.LIMB_SIZE / 3, // height
                (Limb.LimbTyp.RED),
                // values of initial generation
                0.9f,// density
                0.3f, // friction
                0.8f,// restitution
                tr.q.getAngle(),// angle
                this
        );
        limb2.setupBody();


        limbs.add(limb1);
        limbs.add(limb2);


        final Vec2 center1 = limb1.bodyd2.getWorldCenter();
        final Vec2 center2 = limb2.bodyd2.getWorldCenter();
        Vec2 AtoB = center2.sub(center1);
        Vec2 rotCentre = center1.add(AtoB.mulLocal(0.5f));

        FlappingJointDef j12 = new FlappingJointDef();
        j12.motorSpeed = 1f * MathUtils.PI;
        j12.maxMotorTorque = 5000f;  // max Kraft des Motors
        j12.enableMotor = true;
        j12.lowerAngle = -45 * MathUtils.DEG2RAD;
        j12.upperAngle = 45 * MathUtils.DEG2RAD;
        j12.enableLimit = true;
        j12.enableFlapping = true;
        j12.lowerFlapping = -40 * MathUtils.DEG2RAD;
        j12.upperFlapping = +40 * MathUtils.DEG2RAD;
        j12.collideConnected = false;

        j12.initialize(limb1.bodyd2, limb2.bodyd2, rotCentre);
        RevoluteJoint joint = (RevoluteJoint) Utils.world.createJoint(j12);

        // store joint and its def to the biot
        limbJoints.put(joint, j12);

        birthEnergy = Utils.InitialBIRTHENERGY;
        energy = Utils.InitialBIRTHENERGY / 8f;

    }

    public Biot(Biot mom) {
//        System.err.println("mom = " + mom);
        this.id = s_IDMAX++;
        generation = mom.generation + 1;
        birthEnergy = mom.getBirthEnergy() * Utils.RandomInPercentageRange(0.005f);
        energy = mom.getBirthEnergy() / 8f;
        maxAge = Math.round(mom.getMaxAge() * Utils.RandomInPercentageRange(0.005f));
        age = mom.getMaxAge();

//        System.err.println("birthEnergy = " + birthEnergy);

        Iterator<Transform> offIT = mom.limbOffset.iterator();
        Iterator<Limb> limbIt = mom.limbs.iterator();
        Iterator<FlappingJointDef> jointDefIt = mom.limbJoints.values().iterator();
        // where to place the new limb ?? close to mum! plus (10,10) ??
        // todo:  in the upper quadrant of the box - for now!
        final Vec2 initPos = new Vec2(Utils.RandomInRangeNoMRATE(Utils.WIDTHd2 / 2, Utils.WIDTHd2 - 3 * Utils.LIMB_SIZE),
                Utils.RandomInRangeNoMRATE(Utils.HEIGHTd2 / 2, Utils.HEIGHTd2 - 3 * Utils.LIMB_SIZE));
        Transform position = new Transform(initPos, new Rot());
        Transform offset = new Transform();
        Limb prev = null;

//        int counter = 0;
        while (limbIt.hasNext()) {
            Limb momsLimb = limbIt.next();
            position = new Transform(position.p.add(offset.p), new Rot(position.q.getAngle() + offset.q.getAngle()));
            Limb cur = momsLimb.createSimilar(this, position);
            cur.setupBody();
            limbs.add(cur);
//            System.err.println("created limb " + counter + " at " + position);

//            counter++;
            if (limbIt.hasNext()) {
                // compute the offset for the next limb
                Transform momTr = offIT.next();

                final Vec2 vec2 = Utils.RandomUnitVector().mulLocal(Utils.RandomInRange(0.0f, 0.03f) * momTr.p.length());
                final float angle = Utils.RandomInRange(-10 * MathUtils.DEG2RAD, 10 * MathUtils.DEG2RAD);


                offset = new Transform(momTr.p.add(vec2),
                        new Rot(momTr.q.getAngle() + angle));

                limbOffset.add(offset);
            }

            if (prev != null) {
                // connect this limb with the previous
                final Vec2 center1 = prev.bodyd2.getWorldCenter();
                final Vec2 center2 = cur.bodyd2.getWorldCenter();
                Vec2 AtoB = center2.sub(center1);
                Vec2 rotCentre = center1.add(AtoB.mulLocal(0.5f));

                FlappingJointDef def = jointDefIt.next();

                FlappingJointDef j12 = new FlappingJointDef();
                j12.motorSpeed = def.motorSpeed + Utils.RandomInRange(-def.motorSpeed * 0.2f, def.motorSpeed * 0.2f);
                j12.maxMotorTorque = def.maxMotorTorque * Utils.RandomInPercentageRange(0.2f);  // max Kraft des Motors
                j12.enableMotor = Utils.RandomBoolean(0.005f) ? !def.enableMotor : def.enableMotor;
                j12.lowerAngle = def.lowerAngle + Utils.RandomInRange(-5 * MathUtils.DEG2RAD, 5 * MathUtils.DEG2RAD);
                j12.upperAngle = def.upperAngle + Utils.RandomInRange(-5 * MathUtils.DEG2RAD, 5 * MathUtils.DEG2RAD);

                float tmp;
                if (j12.lowerAngle > j12.upperAngle) {
                    tmp = j12.lowerAngle;
                    j12.lowerAngle = j12.upperAngle;
                    j12.upperAngle = tmp;
                }

                j12.enableLimit = Utils.RandomBoolean(0.005f) ? !def.enableLimit : def.enableLimit;
                j12.enableFlapping = Utils.RandomBoolean(0.005f) ? !def.enableFlapping : def.enableFlapping;

                j12.lowerFlapping = def.lowerFlapping + Utils.RandomInRange(-5 * MathUtils.DEG2RAD, 5 * MathUtils.DEG2RAD);
                j12.upperFlapping = def.lowerFlapping + Utils.RandomInRange(-5 * MathUtils.DEG2RAD, 5 * MathUtils.DEG2RAD);
                if (j12.lowerFlapping > j12.upperFlapping) {
                    tmp = j12.lowerFlapping;
                    j12.lowerFlapping = j12.upperFlapping;
                    j12.upperFlapping = tmp;
                }
                j12.collideConnected = false;

                j12.initialize(prev.bodyd2, cur.bodyd2, rotCentre);
                RevoluteJoint joint = (RevoluteJoint) Utils.world.createJoint(j12);

                // store joint and its def to the biot
                limbJoints.put(joint, j12);
            }

            prev = cur;
        }
//        System.err.println("born " + this);
    }

//    protected Vec2 getPostion() {
//        return limbs.get(0).bodyd2.getPosition();
//    }

    public boolean isDead() {
        return age <= 0 || energy <= 0;
    }

    public long deadSince() {
        if (age < 0) return -age;
        else return 0;
    }

    /*
    - Destroy all bodies from d2 world
    - remove the FX Nodes
     */
    public void destroyBiot(Group root) {
        for (Limb next : limbs) {
            next.destroy(root);
        }
    }

    /*
    Update the following things:
    - update age of biot
    - update energy according to collisions
    - Joint angles, motors limits etc.

    - fill died Biots
    - fill pregnant Biots
     */
    public void updateState(ArrayList<Biot> died, TreeSet<Biot> mammies, Biot selected) {
        for (Limb next : limbs) {
            /// in any case: draw the limbs
            next.drawLimb();
        }

        StringBuilder sb = new StringBuilder("Biot.updateState: " + this);

        if (died.contains(this)) {
            return;
        }

        age -= 1;
        final boolean debug = (selected == this && age % 10 == 0);

        if (age <= 0) {
            // dead limbs are re-added
            final boolean add = died.add(this);
            if (debug && add)
                sb.append(": too old!\n ");
            turnOffAllJoints();
            return;
        }


        // iterate of all limbs to get the total energy delta
        float deltaEnergy = 0;
        for (Limb next : limbs) {
            // I hate iterating twice over all limbs!! This must be reworked to be only one loop!

            deltaEnergy += next.updateEnergy(debug, sb);
            /// update Limb drawing in the same loop
//            next.drawLimb();
        }


//        updateEnergy:
        energy += deltaEnergy;

        if (energy >= birthEnergy) {
            mammies.add(this);
        }

        if (debug) {
//            System.err.println(sb.toString() + " total delta Energy: " + deltaEnergy);
            System.err.println("selected: " + this);
        }


        // we could introduce a minimal energy level (then giving birth could mean to die!)
        if (energy <= 0) {
            if (age > 0) {
                age = 0;
            }
            died.add(this);
            turnOffAllJoints();
            return;
        }

//  iterate over the motors and joints... etc
        for (Map.Entry<RevoluteJoint, FlappingJointDef> next : limbJoints.entrySet()) {

            RevoluteJoint joint = next.getKey();
            FlappingJointDef def = next.getValue();

            if (def.enableFlapping) {
                boolean atLowerLimit = joint.getJointAngle() <= def.lowerFlapping;
                boolean atUpperLimit = joint.getJointAngle() >= def.upperFlapping;
                if (atLowerLimit) {
                    joint.setMotorSpeed(Math.abs(joint.getMotorSpeed()));
                } else if (atUpperLimit)
                    joint.setMotorSpeed(-Math.abs(joint.getMotorSpeed()));
            }

            // here there fit a lot of ideas:
            // change
            //  - angle motor power, joint speed, etc
            // depending
            // - on raycast,
            // - on collision type
            // - on energy etc.
        }
    }

    void turnOffAllJoints() {
        for (RevoluteJoint next : limbJoints.keySet()) {
            next.setMaxMotorTorque(0);
            next.setMotorSpeed(0);
            next.enableMotor(false);
        }
    }

    @Override
    public String toString() {
        return String.format("Biot#%04d gen%-4d age=%4d energy=%9.1f", id, generation, age, energy);
    }

    public void goLive(Group root, EAMainFXandBox2d eaMainFXandBox2d) {
        for (Limb limb : limbs) {
            limb.createBodyAndNode(root);
            limb.node.setOnMousePressed(new EAMainFXandBox2d.MouseEventEventHandler(eaMainFXandBox2d, limb, root));
        }
    }

    public AABB getAABB() {
        AABB aabb = new AABB(new Vec2(Float.MAX_VALUE, Float.MAX_VALUE), new Vec2(Float.MIN_VALUE, Float.MIN_VALUE));
        for (Limb limb : limbs) {
            limb.combine(aabb);
        }
        return aabb;
    }

    public Biot createChild(ArrayList<Biot> died) {
        energy -= birthEnergy;  // giving birth costs a lot!
        if (isDead()) died.add(this);
        return new Biot(this);
    }

    public float getBirthEnergy() {
        return birthEnergy;
    }

    public long getMaxAge() {
        return maxAge;
    }


//    @Override
//    public Long getTag(Joint joint) {
//        if (joint == m_joint)
//            return JOINT_TAG;
//        return super.getTag(joint);
//    }
//
//    @Override
//    public void processJoint(Joint joint, Long tag) {
//        if (tag == JOINT_TAG) {
//            m_joint = (RevoluteJoint) joint;
//            isLeft = m_joint.getMotorSpeed() > 0;
//        } else {
//            super.processJoint(joint, tag);
//        }
//    }


}
