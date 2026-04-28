package fractal.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * An object to maintain the image.  Internally, it stores a WritableImage object that
 * can be used to display within a GUI or written to a file.
 *
 * This is the object that we'll share between threads.
 * For this exercise, we won't synchronize threads' access to this object because we'll
 * take care to make sure that only ONE thread will write to a given pixel.  The GUI will ONLY
 * read (to display the image in progress), and we will accept that the GUI may see data that
 * is not completely up to date.
 */
public class FractalImage {

    // The image
    private WritableImage image;

    // The number of iterations that corresponds to the end of the color scale
    private static final int COLOR_SCALE_ITERATIONS = 20;

    // The color scale
    private static final int[] COLORS = {0xff85c1c8, 0xff90a1be, 0xff9c8184, 0xffa761aa,
            0xffaf4980, 0xffb83055, 0xffc0182a, 0xffc80000, 0xffd33300, 0xffde6600,
            0xffe99900, 0xfff4cc00, 0xffffff00};

    // Image dimensions
    private int width;
    private int height;

    public FractalImage( int w, int h ) {
        width = w;
        height = h;
        image = new WritableImage(w, h);
    }

    /**
     * Write a color to a pixel based on the number of iterations.  This will be called by
     * multiple worker threads, but we must take care that there is only ONE thread that writes
     * to a given pixel.
     * @param x the x pixel coordinate
     * @param y the y pixel coordinate
     * @param iterations the number of iterations
     */
    public void writePixel( int x, int y, int iterations ) {
        PixelWriter writer = image.getPixelWriter();
        int color;
        if( iterations == 0 ) color = 0xff000000;
        else {
            double f = ((double) iterations / COLOR_SCALE_ITERATIONS) * COLORS.length;
            int index = (int)f;
            if( index >= COLORS.length ) index = COLORS.length - 1;
            color = COLORS[ index ];
        }
        writer.setArgb(x, y, color);
    }

    /**
     * Draw the image to the provided GraphicsContext context.  This should ONLY be called on the
     * GUI event dispatch thread.
     * @param g a GraphicsContext object
     */
    public void draw(GraphicsContext g) {
        g.drawImage(image, 0, 0);
    }

    /**
     * Clear image to black.
     */
    public void clear() {
        image = new WritableImage(width, height);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
