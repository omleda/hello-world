package tutorialFX;

/**
 * Created by ea on 28.03.15.
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import java.util.ArrayList;

/**
 * @author dilip
 */
public class EAMainFXandBox2d extends Application {

    private Biot selected;
    private Rectangle selectionFrame;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

//        Titel bauen
        primaryStage.setTitle("Biots World!");
        primaryStage.setFullScreen(false);
        primaryStage.setResizable(false);

        final Group root = new Group(); //Create a group for holding all objects on the screen
        final Scene scene = new Scene(root, Utils.WIDTH_px, Utils.HEIGHT_px, Color.BLACK);

        //array for hold the biots
        final ArrayList<Biot> allBiots = new ArrayList<>();


        // setup the contact listener for colliding limbs
        Utils.world.setContactListener(new MyContactListener());

        Utils.fourWalls();


        /**
         * Generate initial biots at random position.
         */
        for (int i = 0; i < Utils.NO_OF_INITIAL_BIOTS; i++) {
            goLive(root, allBiots, new Biot(0,
                    Utils.r.nextFloat() * (Utils.WIDTHd2 - 3 * Utils.LIMB_SIZE) + Utils.LIMB_SIZE,
                    Utils.r.nextFloat() * (Utils.HEIGHTd2 - 3 * Utils.LIMB_SIZE) + Utils.LIMB_SIZE));
        }


        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        Duration duration = Duration.seconds(Utils.DT); // Set duration for frame.

        //Create an ActionEvent, on trigger it executes a world time step and moves the balls to new position
        EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                //Create time step. Set Iteration count 8 for velocity and 3 for positions
                Utils.world.step(Utils.DT, 8, 3);

                //Move balls to the new position computed by JBox2D
                final ArrayList<Biot> died = new ArrayList<>();
                final ArrayList<Biot> mammies = new ArrayList<>();

                for (Biot biot : allBiots) {
                    biot.updateState(died, mammies, selected);


                    if (biot == selected) {
                        updateSelectionFrame(biot, root);
                    }
                }

                for (Biot dead : died) {
                    assert dead.isDead() : dead + " is not dead!? ";
                    if (dead.deadSince() > (3 / Utils.DT)) { // let zombies float around for 3s
                        dead.destroyBiot(root);
                        allBiots.remove(dead);
                    }
                }

                for (Biot mom : mammies) {
                    if (allBiots.size() < Utils.MAX_BIOTS) {
                        final Biot child = mom.createChild();
                        goLive(root, allBiots, child);
                    }
                }
            }
        };



        /**
         * Set ActionEvent and duration to the KeyFrame.
         * The ActionEvent is trigged when KeyFrame execution is over.
         */
        KeyFrame frame = new KeyFrame(duration, ae, null, null);

        timeline.getKeyFrames().add(frame);

        //Create button to start simulation.
        final Button btn = new Button();
        btn.setLayoutX((Utils.WIDTH_px / 2));
        btn.setLayoutY((Utils.HEIGHT_px - 30));
        btn.setText("Start");
        btn.setOnAction(event -> {
            timeline.playFromStart();
            btn.setVisible(false);
        });

        //Add button to the root group
        root.getChildren().add(btn);

        //Draw hurdles on mouse event.
        EventHandler<MouseEvent> addHurdle = me -> {
            //Get mouse's x and y coordinates on the scene
            double dragX = me.getSceneX();
            double dragY = me.getSceneY();

            //Draw ball on this location. Set balls body type to static.
            Ball hurdle = new Ball(Utils.toPosX(dragX), Utils.toPosY(dragY), 2, BodyType.STATIC, Color.BLUE);
            //Add ball to the root group
            root.getChildren().add(hurdle.node);
        };

        scene.setOnMouseDragged(addHurdle);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateSelectionFrame(Biot biot, Group root) {
        assert biot == selected;
        if (biot.isDead()) {
            // clean up the selection frame!
            selected = null;
            root.getChildren().remove(selectionFrame);
            selectionFrame = null;
        } else {
            AABB aabb = biot.getAABB();
            Vec2 extents = aabb.getExtents();
            final Vec2 center = aabb.getCenter();

            selectionFrame.setLayoutX(Utils.toPixelPosX(center.x - extents.x));
            selectionFrame.setLayoutY(Utils.toPixelPosY(center.y + extents.y));
            selectionFrame.setHeight(Utils.toPixelHeight(2 * extents.y));
            selectionFrame.setWidth(Utils.toPixelWidth(2 * extents.x));
        }
    }


    private void goLive(Group root, ArrayList<Biot> allBiots, final Biot e) {
        e.goLive(root, this);
        allBiots.add(e);
    }

    static class MouseEventEventHandler implements EventHandler<MouseEvent> {
        private final Limb limb;
        private final Group root;
        private EAMainFXandBox2d eaMainFXandBox2d;

        public MouseEventEventHandler(EAMainFXandBox2d eaMainFXandBox2d, Limb limb, Group root) {
            this.limb = limb;
            this.root = root;
            this.eaMainFXandBox2d = eaMainFXandBox2d;
        }

        @Override
        public void handle(MouseEvent event) {
            System.err.println("selected Biot: " + limb.biot);
            eaMainFXandBox2d.selected = limb.biot;
            if (eaMainFXandBox2d.selectionFrame == null) {
                eaMainFXandBox2d.selectionFrame = new Rectangle(0, 0, Color.TRANSPARENT);
                eaMainFXandBox2d.selectionFrame.setStroke(Color.GHOSTWHITE);
                root.getChildren().add(eaMainFXandBox2d.selectionFrame);
            }
            eaMainFXandBox2d.updateSelectionFrame(eaMainFXandBox2d.selected, root);
        }
    }
}