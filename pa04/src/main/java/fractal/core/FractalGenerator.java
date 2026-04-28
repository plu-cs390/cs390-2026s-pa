package fractal.core;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class FractalGenerator {

    // The number of iterations to run.  Set this to a large number so that threads have a lot of work to do.
    public static final int MAX_ITERATIONS = 6000;

    // Start location in the complex plane
    private final double xStart, yStart;

    // Size of a pixel in the complex plane
    private final double xStep, yStep;

    // The image (shared with the GUI thread)
    private FractalImage image;

    private List<FractalGenerationListener> listeners;

    public FractalGenerator(FractalImage img) {
        this.image = img;

        // Lower left corner in the complex plane
        this.xStart = -2.0;
        this.yStart = -1.0;

        double ar = (double)image.getWidth() / image.getHeight();  // Aspect ratio
        double cHeight = 2.0;                // Height in complex plane
        double cWidth = ar * cHeight;        // Width in complex plane

        xStep = cWidth / image.getWidth();
        yStep = cHeight / image.getHeight();

        this.listeners = new ArrayList<>();
    }

    /**
     * Add an object that receives generation events
     * @param listener the listener object.
     */
    public void addListener( FractalGenerationListener listener ) {
        listeners.add(listener);
    }

    /**
     * Iterate the Mandelbrot function for the given pixel.
     * @param px the x location of the pixel (pixel column)
     * @param py the y location of the pixel (pixel row)
     * @return the number of iterations required to reach a magnitude of 2.0 or more, or zero if we reached the
     *         maximum number of iterations.
     */
    private int iterate(int px, int py) {
        int height = image.getHeight();

        // Convert the pixel location to the corresponding location in the complex plane
        double cx = xStep * px + xStart;
        double cy = yStep * (height - py - 1) + yStart;

        // Perform the iterations starting with (0,0)
        double zx = 0.0, zy = 0.0;
        double zlen2 = 0.0;
        int iterations = 0;
        while(iterations < MAX_ITERATIONS && zlen2 <= 4.0 ) {
            double zxNext = zx * zx - zy * zy + cx;
            double zyNext = 2.0 * zx * zy + cy;
            zx = zxNext;
            zy = zyNext;
            zlen2 = zx * zx + zy * zy;
            iterations++;
        }
        if( zlen2 <= 4.0 ) {
            return 0;
        } else {
            return iterations;
        }
    }

    private void drawImage( ) {
        int height = image.getHeight();
        int width = image.getWidth();

        // For each pixel calculate the number of iterations and write the pixel
        // to the image.
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int iterations = iterate(i,j);
                image.writePixel(i,j, iterations);
            }
        }
    }

    /**
     * Generate the fractal image.
     * @param threads the number of threads to use during generation
     */
    public void generate(int threads) {
        // Clear the image
        image.clear();

        long start = System.currentTimeMillis();
        drawImage();
        long end = System.currentTimeMillis();

        // Notify the listeners that the image is complete
        notifyComplete( (end - start) / 1000.0 );
    }

    private void notifyComplete( double time ) {
        for( FractalGenerationListener listener : listeners ) {
            Platform.runLater( () -> listener.imageComplete(time) );
        }
    }
}
