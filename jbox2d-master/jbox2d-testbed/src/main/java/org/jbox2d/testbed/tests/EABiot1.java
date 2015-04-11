package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.testbed.framework.TestbedTest;

import java.util.Random;

public class EABiot1 extends TestbedTest {

  @Override
  public void initTest(boolean deserialized) {
/*
    {
      // one static chain
      BodyDef bd = new BodyDef();
//      bd.type = BodyType.DYNAMIC;
      Body ground = m_world.createBody(bd);

      m_world.setGravity(new Vec2());
      m_world.setParticleDamping(0.0f);

      ChainShape shape = new ChainShape();
      Vec2[] vertices =
          new Vec2[] {new Vec2(-20, 0), new Vec2(20, 0), new Vec2(20, 40), new Vec2(-20, 40)};
      shape.createLoop(vertices, 4);

      FixtureDef fd = new FixtureDef();
      fd.setShape(shape);
      fd.setRestitution(1.0f);
      ground.createFixture(fd);
//      ground.createFixture(shape, 0.0f);
    }

*/


    {
      // welt ohne schwerkraft
      m_world.setGravity(new Vec2());

      // vier wände:
      // the Body
      BodyDef bd = new BodyDef();
      bd.type = BodyType.STATIC;
      bd.setPosition(new Vec2());
      Body staticBody = m_world.createBody(bd);

      FixtureDef fd = new FixtureDef();
      //  parameter die bei allen Fixtures gelten
      fd.setRestitution(1.0f);


      // dann die shapes
      PolygonShape polygonShape = new PolygonShape();
      polygonShape.setAsBox(20f,1f, new Vec2(0,0), 0f);  // BODEN
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

//    m_world.setParticleRadius(0.12f);
//    m_world.setParticleDamping(0.01f);
//    {
//      PolygonShape shape = new PolygonShape();
//      shape.setAsBox(8, 10, new Vec2(-12, 10.1f), 0);
//      ParticleGroupDef pd = new ParticleGroupDef();
//      pd.shape = shape;
//      m_world.createParticleGroup(pd);
//    }
//

    {
      BodyDef bd = new BodyDef();
      bd.type = BodyType.DYNAMIC;
      bd.position.set(0.0f, 4.0f);

      PolygonShape box = new PolygonShape();
//      box.setAsBox(2.0f, 0.1f);

//      m_body = m_world.createBody(bd);
//      m_body.createFixture(box, 1.0f);

      box.setAsBox(0.25f, 0.25f);

// m_x = -0.06530577f;

      createABox(bd, box);
      createABox(bd, box);
      createABox(bd, box);

      createABox(bd, box);
      createABox(bd, box);
      createABox(bd, box);

      createABox(bd, box);
      createABox(bd, box);
      createABox(bd, box);




    }


  }

  private void createABox(BodyDef bd, PolygonShape box) {
    float m_x = new Random().nextFloat();
    bd.position.set(m_x*2, 10.0f);
    bd.bullet = false;

    Body m_bullet = m_world.createBody(bd);
    m_bullet.createFixture(box, 100.0f);

    m_bullet.setLinearVelocity(new Vec2(0.0f, -50.0f));
    m_bullet.setAngularVelocity(0.0f);

    FixtureDef fd = new FixtureDef();
    fd.setShape(box);
    fd.setRestitution(1.0f);
    fd.setFriction(0.0f);

  }

  @Override
  public String getTestName() {
     return EABiot1.class.getSimpleName();
  }


}
