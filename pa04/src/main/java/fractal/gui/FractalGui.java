package fractal.gui;

import fractal.core.FractalGenerationListener;
import fractal.core.FractalGenerator;
import fractal.core.FractalImage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The GUI for our fractal generator.
 */
public class FractalGui extends VBox implements FractalGenerationListener {

    // Image dimensions
    private static final int IMAGE_WIDTH = 1024;
    private static final int IMAGE_HEIGHT = 768;

    // The fractal image
    private FractalImage image;

    // The panel that displays the image
    private final Canvas imagePanel;

    // Input/output GUI components
    private TextField numThreadsTf;
    private TextField executionTimeTf;
    private Button startButton;

    public FractalGui() {
        image = new FractalImage(IMAGE_WIDTH, IMAGE_HEIGHT);
        imagePanel = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT);
        buildGui();
    }

    private void buildGui() {
        numThreadsTf = new TextField();
        numThreadsTf.setPrefWidth(50);
        numThreadsTf.setText("1");
        startButton = new Button("Start");
        startButton.setOnAction( e -> startButtonAction() );
        executionTimeTf = new TextField();
        executionTimeTf.setPrefWidth(100);
        executionTimeTf.setText("--");
        executionTimeTf.setEditable(false);

        HBox topPanel = new HBox();
        topPanel.setSpacing(10);
        topPanel.setAlignment(Pos.CENTER);
        VBox.setMargin(topPanel, new Insets(10));
        topPanel.getChildren().addAll(
                new Label("Number of threads: "),
                numThreadsTf, startButton
        );

        HBox bottomPanel = new HBox();
        bottomPanel.setSpacing(10);
        bottomPanel.setAlignment(Pos.CENTER);
        VBox.setMargin(bottomPanel, new Insets(10));
        bottomPanel.getChildren().addAll(
                new Label("Time: "), executionTimeTf
                );

        this.getChildren().addAll(
                topPanel, imagePanel, bottomPanel
        );
    }

    /**
     * This runs the Mandelbrot generator, and displays the results to the screen.
     * This is called when the start button is clicked.
     * Currently, all work is done on the event dispatch thread, so the GUI will
     * freeze.  Your job is to modify this so that the work is done by some number of
     * worker threads and update the GUI while the computation is running.
     * Be careful to make sure that ANYTHING that updates the GUI is done on the event
     * thread ONLY.  The image should be updated regularly during the
     * process.  The GUI should be responsive throughout and should not freeze.
     */
    private void startButtonAction() {
        GraphicsContext g = imagePanel.getGraphicsContext2D();
        g.clearRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        FractalGenerator generator = new FractalGenerator(image);
        generator.addListener(FractalGui.this);

        // Disable the input elements while generating
        startButton.setDisable(true);
        numThreadsTf.setEditable(false);

        // Get the number of threads
        int numThreads = Integer.parseInt(numThreadsTf.getText());

        // Start the generator
        generator.generate(numThreads);
    }

    @Override
    public void imageChanged() {
        image.draw( imagePanel.getGraphicsContext2D() );
    }

    @Override
    public void imageComplete(double time) {
        image.draw(imagePanel.getGraphicsContext2D());

        // Enable the input elements
        startButton.setDisable(false);
        numThreadsTf.setEditable(true);

        // Display elapsed time
        executionTimeTf.setText(String.format("%.3f s", time));
    }
}
