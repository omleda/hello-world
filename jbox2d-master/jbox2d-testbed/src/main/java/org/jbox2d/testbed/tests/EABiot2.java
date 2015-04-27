package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

public class EABiot2 extends TestbedTest {

    private static final long JOINT_TAG = 1;
    private RevoluteJoint m_joint;
    private boolean isLeft;

    @Override
    public void initTest(boolean deserialized) {


        {
            // welt ohne schwerkraft
            m_world.setGravity(new Vec2(0,0));

            // vier wände:
            // in einem Body.
            BodyDef bd = new BodyDef();
            bd.type = BodyType.STATIC;
            bd.setPosition(new Vec2());
            Body staticBody = m_world.createBody(bd);

            FixtureDef fd = new FixtureDef();
            //  parameter die bei allen Fixtures gelten
            fd.setRestitution(1.0f);

            // dann die shapes
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(20f, 1f, new Vec2(0, 0), 0f);  // BODEN
            // box von -20 bis 20 bei y=0
            fd.setShape(polygonShape); // zur FD
            staticBody.createFixture(fd); // diese fixture muss zum Body

            polygonShape.setAsBox(20f, 1f, new Vec2(0, 40), 0f);  // Decke
            fd.setShape(polygonShape); // zur FD
            staticBody.createFixture(fd); // diese fixture muss zum Body

            polygonShape.setAsBox(1f, 20f, new Vec2(-20, 20), 0f);  // Linke Wand
            fd.setShape(polygonShape); // zur FD
            staticBody.createFixture(fd); // diese fixture muss zum Body


            polygonShape.setAsBox(1f, 20f, new Vec2(20, 20), 0f);  // rechte Wand
            fd.setShape(polygonShape); // zur FD
            staticBody.createFixture(fd); // diese fixture muss zum Body


        }


        {
            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.angularDamping =(0.0f);
//            bd.position.set(0.0f, 4.0f);

            PolygonShape box = new PolygonShape();
            final Vec2 center1 = new Vec2(0, 20);  // in der Mitte der Wände
            box.setAsBox(3f, 5f, center1, 3 * MathUtils.DEG2RAD); // positiv is counter-clockwise

            FixtureDef fd = new FixtureDef();
            fd.setShape(box);
            fd.setRestitution(0.0f);
            fd.setFriction(0.0f);
            fd.setDensity(1.0f);


            Body b1 = m_world.createBody(bd);
            b1.createFixture(fd);


            box = new PolygonShape();
            final Vec2 center2 = new Vec2(5, 20);  // rechts von der Mitte der Wände
            box.setAsBox(3f, 5f, center2, -3 * MathUtils.DEG2RAD); // positiv is counter-clockwise

//            FixtureDef fd = new FixtureDef();
            fd.setShape(box);
//            fd.setRestitution(0.0f);
//            fd.setFriction(0.0f);
            fd.setDensity(20.0f);

            Body b2 = m_world.createBody(bd);
            b2.createFixture(fd);


            ///////   joining them:
            RevoluteJointDef rjd = new RevoluteJointDef();

            // between the two centers anchorPoints
//            rjd.localAnchorA = b1.getPosition();
//            rjd.localAnchorB = b2.getPosition();

            Vec2 AtoB = center2.sub(center1);
            System.err.println("AtoB = " + AtoB);

            Vec2 rotCentre = center1.add(AtoB.mul(0.5f));
            System.err.println("rotCentre = " + rotCentre);

            rjd.initialize(b1, b2, rotCentre);
            rjd.motorSpeed = 10.0f * MathUtils.PI;
            rjd.maxMotorTorque = 900f;  // max Kraft des Motors
            rjd.enableMotor = true;
            rjd.lowerAngle = -45 * MathUtils.DEG2RAD;
            rjd.upperAngle = 45 * MathUtils.DEG2RAD;
            rjd.enableLimit = true;
            rjd.collideConnected = false;

            m_joint = (RevoluteJoint) m_world.createJoint(rjd);

        }


    }

    @Override
    public void step(TestbedSettings settings) {
        super.step(settings);
        addTextLine("Limits " + (m_joint.isLimitEnabled() ? "on" : "off") + ", Motor: speed="
                + m_joint.getMotorSpeed() + (isLeft ? "left" : "right") + " torque=" + m_joint.getMotorTorque(1f)
                + " torqueReact=" + m_joint.getReactionTorque(1f));
        addTextLine("Motor: angle=" + m_joint.getJointAngle());


        // simluation to enable some kind of flapping
        if (m_joint.isLimitEnabled()) {
            boolean atLowerLimit = m_joint.getJointAngle() <= m_joint.getLowerLimit() + 40 * MathUtils.DEG2RAD;
            boolean atUpperLimit = m_joint.getJointAngle() >= m_joint.getUpperLimit() -  40 * MathUtils.DEG2RAD;

            if (atLowerLimit ) {
                m_joint.setMotorSpeed(Math.abs(m_joint.getMotorSpeed()));
            } else if (atUpperLimit)
                m_joint.setMotorSpeed(-Math.abs(m_joint.getMotorSpeed()));

        }
    }

    @Override
    public String getTestName() {
        return EABiot2.class.getSimpleName();
    }


}
