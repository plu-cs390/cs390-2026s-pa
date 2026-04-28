package fractal;

import fractal.gui.FractalGui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scn = new Scene( new FractalGui() );
        primaryStage.setScene(scn);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
