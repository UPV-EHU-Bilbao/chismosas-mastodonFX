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

        Account account = item.getAccount();

        content.getEngine().loadContent(item.getContent());
        date.setText(item.getCreatedAt());
        displayName.setText(account.getDisplayName());
        userName.setText("@" + account.getUsername());
        avatar.setImage(new Image(account.getAvatar()));

        disableButtons(item);
        like.setText(String.valueOf(item.getFavouritesCount()));
        retweet.setText(String.valueOf(item.getReblogsCount()));
        comment.setText(String.valueOf(item.getRepliesCount()));

        setText(null);
        setGraphic(loader.getRoot());
    }
    void disableButtons(Status item){
        if (item.getFavouritesCount() == 0){
            likeBtn.setOpacity(0.1);
        }
        if (item.getReblogsCount() == 0){
            retweetBtn.setOpacity(0.1);
        }
        if (item.getRepliesCount() == 0){
            commentBtn.setOpacity(0.1);
        }
    }
}
