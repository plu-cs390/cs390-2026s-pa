package poi.client.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Optional;

public class SearchUI extends VBox {

    private TextField latField;
    private TextField longField;
    private TextField radiusField;
    private ListView<PoiListItem> poiList;

    public SearchUI() {
        buildUI();
    }

    private void buildUI() {
        this.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID,
                        new CornerRadii(5), BorderWidths.DEFAULT,
                        new Insets(10,10,10,10)) )
        );
        this.setPadding( new Insets(10,10,10,10));
        HBox locationBox = new HBox();
        Label latLabel = new Label("Latitude: ");
        latLabel.setPadding( new Insets(0, 10, 0, 10));
        Label longLabel = new Label("Longitude: ");
        longLabel.setPadding( new Insets(0, 10, 0, 10));
        Label radiusLabel = new Label("Radius (miles): ");
        radiusLabel.setPadding( new Insets(0, 10, 0, 10));

        latField = new TextField();
        latField.setText("47.60865");
        longField = new TextField();
        longField.setText("-122.34059");
        radiusField = new TextField();

        locationBox.getChildren().addAll(latLabel, latField, longLabel, longField);

        HBox radiusBox = new HBox();

        Button searchButton = new Button("Search");
        HBox.setMargin(searchButton, new Insets(0,0,0,20));
        VBox.setMargin(radiusBox, new Insets(5, 0, 10, 0));
        VBox.setMargin(locationBox, new Insets(10, 0, 5, 0));

        radiusBox.getChildren().addAll(radiusLabel, radiusField, searchButton);

        poiList = new ListView<>();
        poiList.setPrefHeight(300);
        poiList.setPlaceholder(new Label("No POI found"));

        this.getChildren().addAll(locationBox, radiusBox, poiList);
    }

    public Optional<PoiListItem> getSelectedPoi() {
        PoiListItem item = poiList.getSelectionModel().getSelectedItem();
        if( item == null ) return Optional.empty();
        return Optional.of(item);
    }
}
