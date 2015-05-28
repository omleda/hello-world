package tutorialFX;

import org.jbox2d.dynamics.joints.RevoluteJointDef;

/**
 * Created by ea on 17.05.15.
 */
public class FlappingJointDef extends RevoluteJointDef {

    /**
     * A flag to enable flapping.
     */
    public boolean enableFlapping;

    /**
     * Angels for flapping: to change Motor direction
     */
    public float upperFlapping;
    public float lowerFlapping;

}
