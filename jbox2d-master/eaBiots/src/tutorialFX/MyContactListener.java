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
        final Object limba = contact.getFixtureA().getUserData();
        final Object limbb = contact.getFixtureB().getUserData();

        if (limba instanceof Limb && limbb instanceof Limb) {
            ((Limb) limba).startContact((Limb) limbb);
            ((Limb) limbb).startContact((Limb) limba);
        }
        // what else??

    }

    @Override
    public void endContact(Contact contact) {
        final Object limba = contact.getFixtureA().getUserData();
        final Object limbb = contact.getFixtureB().getUserData();

        if (limba instanceof Limb && limbb instanceof Limb) {
            ((Limb) limba).endContact((Limb) limbb);
            ((Limb) limbb).endContact((Limb) limba);
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
