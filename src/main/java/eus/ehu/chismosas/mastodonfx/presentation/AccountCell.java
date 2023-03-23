package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import social.bigbone.api.entity.Account;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import social.bigbone.api.entity.Status;


public class AccountCell extends ListCell<Account> {

    @FXML
    private ImageView avatar;

    @FXML
    private Label displayName;

    @FXML
    private Label userName;

    private FXMLLoader loader;

    @Override
    protected void updateItem(Account item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("account.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        displayName.setText(item.getDisplayName());
        userName.setText(item.getUsername());
        avatar.setImage(new Image(item.getAvatar()));

        setText(null);
        setGraphic(loader.getRoot());
    }
}
