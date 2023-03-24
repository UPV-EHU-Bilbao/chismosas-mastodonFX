package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import social.bigbone.api.entity.Status;
import javafx.scene.web.WebView;


public class StatusCell extends ListCell<Status> {

    private FXMLLoader loader;

    @FXML
    private ImageView avatar;

    @FXML
    private WebView content;

    @FXML
    private Label date;

    @FXML
    private Label displayName;

    @FXML
    private Label userName;

    @Override
    /**
     * Update the status in the item list
     * @param item, status to update
     * @param empty, if the list is empty
     * @return void
     */
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("status.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        content.getEngine().loadContent(item.getContent());
        date.setText(item.getCreatedAt());
        displayName.setText(item.getAccount().getDisplayName());
        userName.setText(item.getAccount().getUsername());
        avatar.setImage(new Image(item.getAccount().getAvatar()));

        setText(null);
        setGraphic(loader.getRoot());
    }
}
