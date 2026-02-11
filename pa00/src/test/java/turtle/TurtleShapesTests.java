package turtle;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurtleShapesTests {

    @Test
    public void test_chordLengthNegativeRadius() {
        assertThrows(IllegalArgumentException.class, () -> TurtleShapes.chordLength(-1.0, 0.0) );
    }

    @Test
    public void test_chordLengthNegativeAngle() {
        assertThrows(IllegalArgumentException.class, () -> TurtleShapes.chordLength(1.0, -1.0) );
    }

    @Test
    public void test_chordLengthAngleTooLarge() {
        assertThrows( IllegalArgumentException.class, () -> TurtleShapes.chordLength(1.0, 181.0) );
    }

    @Test
    public void test_chordLengthSmallAngle() {
        double result = TurtleShapes.chordLength(10.0, 1.0);
        assertEquals( 0.1745307, result, 0.00001 );
    }

    @Test
    public void test_chordLengthLargeAngle() {
        double result = TurtleShapes.chordLength(10.0, 179.0);
        assertEquals( 19.999238, result, 0.00001 );
    }

    @Test
    public void test_chordLengthMidAngle() {
        double result = TurtleShapes.chordLength(10.0, 90.0);
        assertEquals( 14.142136, result, 0.00001 );
    }

    @Test
    public void test_chordLengthMidAngleShortRadius() {
        double result = TurtleShapes.chordLength(1.0, 90.0);
        assertEquals( 1.4142136, result, 0.00001 );
    }

    @Test
    public void test_chordLengthMidAngleLongRadius() {
        double result = TurtleShapes.chordLength(1000.0, 90.0);
        assertEquals( 1414.21356, result, 0.00001 );
    }

    @Test
    public void test_headingToPointSmallRightTurn() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        t.turn(30);
        assertEquals( 60.0,
                ts.calculateHeadingToPoint(new Point2D(1,1)), 0.001);
    }

    @Test
    public void test_headingToPointLargeRightTurn() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        t.turn(30);
        assertEquals( 150.0,
                ts.calculateHeadingToPoint(new Point2D(0,0)), 0.001);
    }

    @Test
    public void test_headingToPointLargeLeftTurn() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        t.turn(30);

        assertEquals( -120.0,
                ts.calculateHeadingToPoint(new Point2D(-1,1)), 0.001);
    }

    @Test
    public void test_headingToPointSmallLeftTurn() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        t.turn(30);

        assertEquals( -30.0,
                ts.calculateHeadingToPoint(new Point2D(0,2)), 0.001);
    }

    @Test
    public void test_distanceToPointInteger() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        Point2D pt = new Point2D(10, 1);

        assertEquals( 10.0, ts.distanceToPoint(pt), 0.0001);
    }

    @Test
    public void test_distanceToPointDiagonal1() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        Point2D pt = new Point2D(1, 0);

        assertEquals( 1.41421356, ts.distanceToPoint(pt), 0.0001);
    }

    @Test
    public void test_distanceToPointDiagonal2() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.turn(90);
        t.forward(1);
        Point2D pt = new Point2D(0, 1);

        assertEquals( 1.41421356, ts.distanceToPoint(pt), 0.0001);
    }

    @Test
    public void headingToPoint45() {
        TurtleShapes ts = new TurtleShapes();
        Point2D pt = new Point2D(1, 1);

        assertEquals( 45, ts.calculateHeadingToPoint(pt), 0.0001);
    }

    @Test
    public void headingToPoint30() {
        TurtleShapes ts = new TurtleShapes();
        Turtle t = ts.getTurtle();
        t.forward(1);
        t.turn(30);
        Point2D pt = new Point2D(1, 0);

        assertEquals( 105, ts.calculateHeadingToPoint(pt), 0.0001);
    }
}
