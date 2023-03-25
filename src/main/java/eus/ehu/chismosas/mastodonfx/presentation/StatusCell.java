package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import javafx.scene.web.WebView;

/**
 * This class is used to update and show the status information
 * in the list that will be shown in the main view when button 'profile' is pressed
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
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

    @FXML
    private Label bookmark;

    @FXML
    private Label comment;

    @FXML
    private Label like;

    @FXML
    private Label retweet;

    @FXML
    private ImageView bookmarkBtn;

    @FXML
    private ImageView commentBtn;

    @FXML
    private ImageView likeBtn;

    @FXML
    private ImageView retweetBtn;

    @Override
    /**
     * Updates the status to be shown in the list
     * @param item, status to update
     * @param empty, if the list is empty
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

        Account account = item.getAccount();

        content.getEngine().loadContent(item.getContent());
        date.setText(item.getCreatedAt());
        displayName.setText(account.getDisplayName());
        userName.setText("@" + account.getUsername());
        avatar.setImage(new Image(account.getAvatar()));

        like.setText(String.valueOf(item.getFavouritesCount()));
        retweet.setText(String.valueOf(item.getReblogsCount()));
        comment.setText(String.valueOf(item.getRepliesCount()));

        setText(null);
        setGraphic(loader.getRoot());
    }
}
