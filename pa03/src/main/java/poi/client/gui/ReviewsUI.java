package poi.client.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewsUI extends VBox {

    private ListView<ReviewListItem> reviewListView;
    private ChoiceBox<Pair<String, Integer>> ratingChoice;
    private TextField reviewField;

    public ReviewsUI() {
        buildUI();
    }

    private void buildUI() {
        this.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID,
                        new CornerRadii(5), BorderWidths.DEFAULT,
                        new Insets(10,10,10,10)) )
        );
        this.setPadding( new Insets(10,10,10,10));

        Button getButton = new Button("Get Reviews");
        HBox.setMargin(getButton, new Insets(0, 10, 0, 10));

        Button deleteButton = new Button("Delete Review");

        HBox buttonBox = new HBox();
        VBox.setMargin(buttonBox, new Insets(10, 0, 10, 0));
        buttonBox.getChildren().addAll(getButton, deleteButton);

        reviewListView = new ListView<>();
        reviewListView.setPrefHeight(250);
        reviewListView.setPlaceholder(new Label("No Reviews"));

        HBox reviewBox = new HBox();
        VBox.setMargin(reviewBox, new Insets(10,0,10,0));

        ratingChoice = new ChoiceBox<>();
        ratingChoice.setPrefWidth(100);
        List<Pair<String,Integer>> options = new ArrayList<>();
        options.add(new Pair<>("1", 1));
        options.add(new Pair<>("2", 2));
        options.add(new Pair<>("3", 3));
        options.add(new Pair<>("4", 4));
        options.add(new Pair<>("5", 5));
        ratingChoice.setConverter(new StringConverter<Pair<String, Integer>>() {
            @Override
            public String toString(Pair<String, Integer> p) {
                return p.getKey();
            }

            @Override
            public Pair<String, Integer> fromString(String s) { return null; }
        });
        Pair<String, Integer> emptyPair = new Pair<>("Rating", -1);
        ratingChoice.getItems().add( emptyPair );
        ratingChoice.getItems().addAll(options);
        ratingChoice.setValue(emptyPair);

        reviewField = new TextField();
        reviewField.setPrefWidth(500);
        Button addReviewButton = new Button("Add");
        Button updateReviewButton = new Button("Update");

        HBox.setMargin(updateReviewButton, new Insets(0, 10, 0, 10));
        HBox.setMargin(reviewField, new Insets(0, 10, 0, 10));
        reviewBox.getChildren().addAll(ratingChoice, reviewField, addReviewButton, updateReviewButton);

        this.getChildren().addAll(buttonBox, reviewListView, reviewBox);
    }

    private Optional<ReviewListItem> getSelectedReviewItem() {
        return Optional.ofNullable(reviewListView.getSelectionModel().getSelectedItem());
    }
}
