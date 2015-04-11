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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import java.util.Random;

/**
 * @author dilip
 */
public class EAMainFXandBox2d extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Biots World!");
        primaryStage.setFullScreen(false);
        primaryStage.setResizable(false);

        final Group root = new Group(); //Create a group for holding all objects on the screen
        final Scene scene = new Scene(root, Utils.WIDTH_px, Utils.HEIGHT_px, Color.BLACK);

        //Ball array for hold the  balls
        final Limb[] biot = new Limb[Utils.NO_OF_BALLS];

        Random r = new Random(System.currentTimeMillis());

        /**
         * Generate Biot and position them on random locations.
         * Random locations between 5 to 95 on x axis and between 100 to 500 on y axis
         */
        for (int i = 0; i < Utils.NO_OF_BALLS; i++) {
            biot[i] = new Limb(Utils.WIDTHd2 / 2 + 5 * i, Utils.HEIGHTd2 / 2 + 5 * i);
        }

        //Add ground to the application, this is where balls will land
//        Utils.addGround(100, 10);


        //Add left and right walls so balls will not move outside the viewing area.
//        Utils.addWall(0, 100, 1, 100); //Left wall
//        Utils.addWall(99, 100, 1, 100); //Right wall

        Utils.fourWalls();

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        Duration duration = Duration.seconds(1.0 / 60.0); // Set duration for frame.

        //Create an ActionEvent, on trigger it executes a world time step and moves the balls to new position
        EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                //Create time step. Set Iteration count 8 for velocity and 3 for positions
                Utils.world.step(1.0f / 60.f, 8, 3);

                //Move balls to the new position computed by JBox2D
                for (int i = 0; i < Utils.NO_OF_BALLS; i++) {
                    Body body = (Body) biot[i].node.getUserData();


                    // die richtig postion des FXRechtecks muss ausgerechnet werden:
                    final Transform transform = new Transform();
                    AABB aabb = new AABB(new Vec2(Float.MAX_VALUE, Float.MAX_EXPONENT), new Vec2(Float.MIN_VALUE, Float.MIN_VALUE));

                    Fixture fixture = body.getFixtureList();
                    while (fixture != null) {
                        final Shape shape = fixture.getShape();
                        final int childCount = shape.getChildCount();

                        for (int child = 0; child < childCount; child++) {
                            AABB shapeAABB = new AABB();
                            shape.computeAABB(shapeAABB, transform, child);
                            final float radius = shape.getRadius();
                            aabb.upperBound.x -= radius;
                            aabb.upperBound.y -= radius;
                            aabb.lowerBound.x += radius;
                            aabb.lowerBound.y += radius;
                            aabb.combine(shapeAABB);
                        }
                        fixture = fixture.getNext();
                    }
                    final Vec2 extents = aabb.getExtents();

                    biot[i].node.setLayoutX(Utils.toPixelPosX(body.getPosition().x - extents.x));
                    biot[i].node.setLayoutY(Utils.toPixelPosY(body.getPosition().y + extents.y));
                    biot[i].node.setRotate(Math.toDegrees(body.getAngle()));
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
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                timeline.playFromStart();
                btn.setVisible(false);
            }
        });

        //Add button to the root group
        root.getChildren().add(btn);

        //Add all balls to the root group
        for (int i = 0; i < Utils.NO_OF_BALLS; i++) {
            root.getChildren().add(biot[i].node);
        }

        //Draw hurdles on mouse event.
        EventHandler<MouseEvent> addHurdle = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                //Get mouse's x and y coordinates on the scene
                double dragX = me.getSceneX();
                double dragY = me.getSceneY();

                //Draw ball on this location. Set balls body type to static.
                Ball hurdle = new Ball(Utils.toPosX(dragX), Utils.toPosY(dragY), 2, BodyType.STATIC, Color.BLUE);
                //Add ball to the root group
                root.getChildren().add(hurdle.node);
            }
        };

        scene.setOnMouseDragged(addHurdle);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}