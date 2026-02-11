package turtle;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A GUI for displaying a drawing produced by a Turtle.
 */
public class TurtleGUI extends StackPane {

    private Canvas drawPanel;
    private Turtle turtle;
    private AtomicBoolean drawing;

    private static final int CANVAS_SIZE = 800;
    private static final long ANIMATION_DELAY = 100;

    public TurtleGUI(Turtle t) {
        this.drawPanel = new Canvas(CANVAS_SIZE,CANVAS_SIZE);
        this.turtle = t;
        this.drawing = new AtomicBoolean(false);

        this.getChildren().add(this.drawPanel);
    }

    public void draw() {
        if( ! drawing.compareAndSet(false, true) ) return;

        List<LineSegment> lines = turtle.getLines();
        GraphicsContext g = drawPanel.getGraphicsContext2D();

        AnimationRunnable runnable = new AnimationRunnable(lines, g);
        ExecutorService serv = Executors.newSingleThreadExecutor();
        serv.submit(runnable);
        serv.shutdown();
    }

    private static class AnimationRunnable implements Runnable {
        private final List<LineSegment> lines;
        private final GraphicsContext g;

        AnimationRunnable( List<LineSegment> lines, GraphicsContext g ) {
            this.lines = lines;
            this.g = g;
        }

        @Override
        public void run() {
            int center = CANVAS_SIZE / 2;
            for( LineSegment ls : lines ) {
                Platform.runLater( () -> {
                    g.setStroke(ls.getPenColor());
                    Point2D start = ls.getStart();
                    Point2D end = ls.getEnd();

                    // Translate and invert y
                    g.strokeLine(start.getX() + center,
                            CANVAS_SIZE - (start.getY() + center),
                            end.getX() + center,
                            CANVAS_SIZE - (end.getY() + center));
                });

                try{ Thread.sleep(ANIMATION_DELAY ); }
                catch(InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        }
    }
}
