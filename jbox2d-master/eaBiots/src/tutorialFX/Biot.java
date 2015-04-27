package tutorialFX;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;

/**
 * Created by ea on 23.04.15.
 *
 * A Biot consists of {@link Limb}, they are connected by {@link RevoluteJoint}s
 */
public class Biot {



   final ArrayList<Limb> limbs = new ArrayList<>();
    private final RevoluteJoint joint;


    static Limb createLimb(int i) {
        return new Limb(Utils.WIDTHd2 / 2 + (1 + Utils.LIMB_SIZE) * i,  // Xpos
                        Utils.HEIGHTd2 / 2 + (1 + Utils.LIMB_SIZE) * i, // YPos
                        Utils.LIMB_SIZE, // width
                        Utils.LIMB_SIZE, // height
                        (i % 2 == 0 ? Limb.LimbTyp.EATER : Limb.LimbTyp.ENERGY),
                        0, // generation
                        // values of initial generation
                        0.9f,// density
                        0.3f, // friction
                        0.8f// restitution
                );
    }


    public Biot() {
        final Limb limb1 = new Limb(Utils.WIDTHd2 / 2 + (1 + Utils.LIMB_SIZE) * 0,  // Xpos
                Utils.HEIGHTd2 / 2 + (1 + Utils.LIMB_SIZE) * 0, // YPos
                Utils.LIMB_SIZE, // width
                Utils.LIMB_SIZE, // height
                Limb.LimbTyp.EATER,
                0, // generation
                // values of initial generation
                0.9f,// density
                0.3f, // friction
                0.8f// restitution
        );
        Limb.setupBody(limb1);

        final Limb limb2 = new Limb(Utils.WIDTHd2 / 2 + (1 + Utils.LIMB_SIZE) * 0.2f,  // Xpos
                Utils.HEIGHTd2 / 2 + (1 + Utils.LIMB_SIZE) * 0.2f, // YPos
                Utils.LIMB_SIZE, // width
                Utils.LIMB_SIZE, // height
                ( Limb.LimbTyp.ENERGY),
                0, // generation
                // values of initial generation
                0.9f,// density
                0.3f, // friction
                0.8f// restitution
        );
        Limb.setupBody(limb2);


        limbs.add(limb1);
        limbs.add(limb2);

        RevoluteJointDef rjd = new RevoluteJointDef();


        rjd.initialize(limb1.bodyd2, limb2.bodyd2, limb1.bodyd2.getPosition());
        rjd.motorSpeed = 10.0f * MathUtils.PI;
        rjd.maxMotorTorque = 500f;
        rjd.enableMotor = true;
        rjd.lowerAngle = -0.25f * MathUtils.PI;
        rjd.upperAngle = 0.5f * MathUtils.PI;
        rjd.enableLimit = true;
        rjd.collideConnected = false;



        joint = (RevoluteJoint) Utils.world.createJoint(rjd);


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
