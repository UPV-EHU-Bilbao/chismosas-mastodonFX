package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.scene.control.ListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import social.bigbone.api.entity.Status;


public class StatusCell extends ListCell<Status> {
    private FXMLLoader loader;

    @FXML
    private AnchorPane listItem;

    @FXML
    private Label ability;

    @FXML
    private Label id;

    @FXML
    private ImageView picture;

    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("pokemon.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        id.setText(item.getId());
       // ability.setText(item.getAbility());
       // picture.setImage(new Image(item.getPicture()));

        setText(null);
        setGraphic(listItem);
    }
}
