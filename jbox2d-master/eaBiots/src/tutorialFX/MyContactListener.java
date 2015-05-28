package tutorialFX;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Created by ea on 11.04.15.
 */
public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        final Object objA = contact.getFixtureA().getUserData();
        final Object objB = contact.getFixtureB().getUserData();

        if (objA instanceof Limb && objB instanceof Limb) {
            final Limb limbA = (Limb) objA;
            final Limb limbB = (Limb) objB;
            if (limbA.biot != limbB.biot) {
                // limbs of the  same biot to physically collide, but
                // energy-wise those collisions are ignored.
                limbA.startContact(limbB);
                limbB.startContact(limbA);
            }
        }
        // what else??
    }

    @Override
    public void endContact(Contact contact) {
        final Object objA = contact.getFixtureA().getUserData();
        final Object objB = contact.getFixtureB().getUserData();

        if (objA instanceof Limb && objB instanceof Limb) {
            final Limb limbA = (Limb) objA;
            final Limb limbB = (Limb) objB;
            if (limbA.biot != limbB.biot) {
                // limbs of the  same biot to physically collide, but
                // energy-wise those collisions are ignored.
                limbA.endContact(limbB);
                limbB.endContact(limbA);
            }
        }
        // what else??
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
//        System.err.println("preSolve contact = " + contact);
//        System.err.println("oldManifold = " + oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
//        System.err.println("postSolve contact = " + contact);
//        System.err.println("impulse = " + impulse);
    }
}
