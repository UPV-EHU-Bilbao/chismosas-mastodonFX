package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
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
    private Text contentText;

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

    private boolean isLiked;


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

        isLiked = status.isFavourited();

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("status.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Document parsedContent = Jsoup.parse(status.getContent());
        contentText.setText(parsedContent.text());
//        List<Element> children = contentDoc.children();
//        List<Element> children = contentDoc.getAllElements();

        date.setText(getPrettyDate(status.getCreatedAt()));

        var account = status.getAccount();
        assert account != null;
        displayName.setText(account.getDisplayName());
        userName.setText("@" + account.getUsername());
        avatar.setImage(new Image(account.getAvatar()));

        like.setText(String.valueOf(status.getFavouritesCount()));
        if (status.isFavourited()) likeBtn.setOpacity(1);
        else likeBtn.setOpacity(0.5);
        retweet.setText(String.valueOf(status.getReblogsCount()));
        comment.setText(String.valueOf(status.getRepliesCount()));

        setText(null);
        setGraphic(loader.getRoot());

    }

    @FXML
    private void onLikeBtn() {
        try {
            if (isLiked) {
                likeBtn.setOpacity(0.5);
                like.setText(String.valueOf(Integer.parseInt(like.getText()) - 1));
                CompletableFuture.runAsync(this::unlike);
                isLiked = false;
            } else {
                likeBtn.setOpacity(1);
                like.setText(String.valueOf(Integer.parseInt(like.getText()) + 1));
                CompletableFuture.runAsync(this::like);
                isLiked = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void like() {
        try {
            BusinessLogic.favouriteStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void unlike() {
        try {
            BusinessLogic.unfavouriteStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the createdAt string of the status to a String to display in the toot date
     * @param createdAt Attribute of the status
     * @return String to display in the toot date
     */
    private String getPrettyDate(String createdAt) {
        var creationDateTime = timeParser.parse(createdAt, OffsetDateTime::from);
        var now = OffsetDateTime.now();
        Duration timeSinceCreation = Duration.between(creationDateTime, now);

        if (timeSinceCreation.toSeconds() < 60)
            return(timeSinceCreation.getSeconds() + "s ago");
        else if (timeSinceCreation.toMinutes() < 60)
            return(timeSinceCreation.toMinutes() + "m ago");
        else if (timeSinceCreation.toHours() < 24)
            return(timeSinceCreation.toHours() + "h ago");
        else if (timeSinceCreation.toDays() < 7)
            return(timeSinceCreation.toDays() + "d ago");
        else if (creationDateTime.getYear() == now.getYear())
            return(timeFormatter.format(creationDateTime));
        else
            return(timeFormatterYear.format(creationDateTime));
    }
}
