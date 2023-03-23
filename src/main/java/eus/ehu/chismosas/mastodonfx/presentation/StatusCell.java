package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.scene.control.ListCell;
import javafx.fxml.FXMLLoader;
import social.bigbone.api.entity.Status;


public class StatusCell extends ListCell<Status> {
    private FXMLLoader loader;

    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setText(null);
        //setGraphic(listItem);
    }
}
