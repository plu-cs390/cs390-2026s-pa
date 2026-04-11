package poi.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import poi.client.gui.ClientUI;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("POI Client");
        stage.setScene( new Scene( new ClientUI() ) );
        stage.show();
    }
}
