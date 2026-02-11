package turtle;


import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Immutable object that represents a line segment produced by the turtle.
 */
public class LineSegment {

    private Point2D start;
    private Point2D end;
    private Color color;

    /**
     * Construct a LineSegment with the given data.
     *
     * @param color the color of the line segment
     * @param st the starting position of the line
     * @param end the end position of the line
     */
    public LineSegment( Color color, Point2D st, Point2D end ) {
        this.start = st;
        this.end = end;
        this.color = color;
    }

    public Point2D getStart() { return start; }
    public Point2D getEnd() { return end; }
    public Color getPenColor() { return this.color; }
}
