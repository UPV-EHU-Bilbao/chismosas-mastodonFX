package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import social.bigbone.api.entity.Account;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import social.bigbone.api.entity.Status;

/**
 * This class is used to update and show the account information
 * in the list that will be shown in the main view when button 'following'
 * or 'followers'are pressed
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class AccountCell extends ListCell<Account> {

    @FXML
    private ImageView avatar;

    @FXML
    private Label content;

    @FXML
    private Label date;

    @FXML
    private Label displayName;

    @FXML
    private Label userName;

    private FXMLLoader loader;

    @Override
    /**
     * Updates the account to be shown in the list
     * @param item, account to add to the list
     * @param empty, if the list is empty
     */
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
        userName.setText("@" + item.getUsername());
        avatar.setImage(new Image(item.getAvatar()));

        setText(null);
        setGraphic(loader.getRoot());
    }
}
