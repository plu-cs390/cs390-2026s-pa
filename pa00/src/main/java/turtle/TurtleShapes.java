package turtle;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

public class TurtleShapes extends Application {

    private final Turtle turtle;

    public TurtleShapes() {
        turtle = new Turtle();
    }

    public Turtle getTurtle() {
        return turtle;
    }

    /**
     * For a circle of radius r, returns the chord length for the given angle.
     *
     * @param r the radius of the circle, must be > 0.0
     * @param angle the angle in degrees, where 0.0 < angle < 180.0
     * @return the chord length
     * @throws IllegalArgumentException if the radius or angle is invalid
     */
    public static double chordLength( double r, double angle ) {
        // TODO: Implement this based on the specification
        return 0.0;
    }

    /**
     * Compute the _change in_ the turtle's current heading that will make it face the
     * given point.
     * TIP: the Math.atan2 function will be helpful here.
     *
     * @param pt the point
     * @return the change in the turtle's current heading that will turn it towards pt.
     */
    public double calculateHeadingToPoint( Point2D pt ) {
        // TODO: Implement this based on the specification above
        return 0.0;
    }

    /**
     * Compute and return the distance from the turtle's current position to the given
     * point.
     *
     * @param pt the point
     * @return the distance from the turtle's current position to pt
     */
    public double distanceToPoint( Point2D pt ) {
        // TODO: Implement this based on the specification above
        return 0.0;
    }

    /**
     * Draw a square.
     * @param sideLength the length of one side of the square
     */
    public void drawSquare( double sideLength ) {
        turtle.forward(sideLength);
        turtle.turn(90);
        turtle.forward(sideLength);
        turtle.turn(90);
        turtle.forward(sideLength);
        turtle.turn(90);
        turtle.forward(sideLength);
        turtle.turn(90);
    }

    /**
     * Draw an approximation of a circle by drawing a many-sided regular polygon, using only right
     * turns.
     *
     * @param r the radius of the circle, must be > 0.0
     * @param sides the number of sides, must be >= 10
     * @throws IllegalArgumentException if the radius is 0.0 or less or sides is less than 10
     */
    public void drawApproximateCircle( double r, int sides ) {
        // TODO: Implement this based on the specification above
    }

    /**
     * Draw a straight line through the given points in the order provided.
     *
     * @param pts a list of points for the turtle to move through
     */
    public void drawThroughPoints( List<Point2D> pts ) {
        // TODO: Implement this based on the specification above
    }

    /**
     * Draw your own, custom piece of art!  Many interesting images can be drawn using this simple turtle.
     * See the assignment for examples.
     */
    public void art( ) {
        // TODO: Implement this based on the specification above
    }

    /**
     * Use this method to test your TurtleShapes methods.
     */
    private void drawShapes() {
        // Replace this with your own manual tests
        drawSquare(345.0);
    }

    // ****** Do not modify the code below ******

    public static void main(String[] args) {
        TurtleShapes.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        drawShapes();
        TurtleGUI gui = new TurtleGUI(turtle);
        primaryStage.setScene(new Scene(gui));
        primaryStage.setTitle("PLU Turtle");
        primaryStage.setOnShown(e -> gui.draw());
        primaryStage.show();
    }
}
