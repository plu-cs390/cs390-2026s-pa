package poi.client.gui;

import javafx.scene.layout.VBox;

public class ClientUI extends VBox {

    public ClientUI() {
        buildUI();
    }

    private void buildUI() {
        SearchUI searchUI = new SearchUI();
        ReviewsUI reviewsUI = new ReviewsUI();
        this.getChildren().addAll(searchUI, reviewsUI);
    }

}
