package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used to update and show the status information
 * in the list that will be shown in the main view when button 'profile' is pressed
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class StatusCell extends ListCell<Status> {
    static final DateTimeFormatter timeParser = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
    static final DateTimeFormatter timeFormatterYear = DateTimeFormatter.ofPattern("MMMM d y", Locale.ENGLISH);

    private FXMLLoader loader;

    private Status status;

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


    /**
     * Updates the status to be shown in the list
     *
     * @param item,  status to update
     * @param empty, if the list is empty
     */
    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        status = item;
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

        // To ensure this gets run on the JavaFX thread
        Platform.runLater(() -> {
            content.getEngine().loadContent(item.getContent());
            setDate(item.getCreatedAt());


            assert account != null;
            displayName.setText(account.getDisplayName());
            userName.setText("@" + account.getUsername());
            avatar.setImage(new Image(account.getAvatar()));

            like.setText(String.valueOf(item.getFavouritesCount()));
            retweet.setText(String.valueOf(item.getReblogsCount()));
            comment.setText(String.valueOf(item.getRepliesCount()));

            setText(null);
            setGraphic(loader.getRoot());
        });

    }

    @FXML
    private void onLike() {
        try {
            if (status.isFavourited()) {
                likeBtn.setOpacity(0.5);
                BusinessLogic.unfavouriteStatus(status.getId());
            } else {
                likeBtn.setOpacity(1);
                BusinessLogic.favouriteStatus(status.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(() -> BusinessLogic.mainController.updateStatus(status.getId()));
    }

    private void setDate(String createdAt) {
        OffsetDateTime creationDateTime = timeParser.parse(createdAt, OffsetDateTime::from);
        OffsetDateTime now = OffsetDateTime.now();
        Duration timeSinceCreation = Duration.between(creationDateTime, now);

        if (timeSinceCreation.toSeconds() < 60)
            date.setText(timeSinceCreation.getSeconds() + "s ago");
        else if (timeSinceCreation.toMinutes() < 60)
            date.setText(timeSinceCreation.toMinutes() + "m ago");
        else if (timeSinceCreation.toHours() < 24)
            date.setText(timeSinceCreation.toHours() + "h ago");
        else if (timeSinceCreation.toDays() < 7)
            date.setText(timeSinceCreation.toDays() + "d ago");
        else if (creationDateTime.getYear() == now.getYear())
            date.setText(timeFormatter.format(creationDateTime));
        else
            date.setText(timeFormatterYear.format(creationDateTime));
    }
}
