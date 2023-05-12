package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import social.bigbone.api.entity.Account;
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

    MainController mainController = MainController.getInstance();


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

    private Status status;
    private Account account;
    private long likes;
    private long reblogs;
    private Parent root;


    /**
     * Updates the status to be shown in the list
     *
     * @param item,  status to update
     * @param empty, if the list is empty
     */
    @Override
    protected void updateItem(Status item, boolean empty) {
        status = item;

        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        account = status.getAccount();
        likes = status.getFavouritesCount();
        reblogs = status.getReblogsCount();

        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("status.fxml"));
            loader.setController(this);
            try {
                loader.load();
                root = loader.getRoot();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Document parsedContent = Jsoup.parse(status.getContent());
        contentText.setText(parsedContent.text());
//        List<Element> children = contentDoc.children();
//        List<Element> children = contentDoc.getAllElements();

        date.setText(getPrettyDate(status.getCreatedAt()));
        displayName.setText(account.getDisplayName());
        userName.setText("@" + account.getUsername());
        avatar.setImage(ImageCache.get(account.getAvatar()));

        like.setText(String.valueOf(likes));
        if (status.isFavourited()) likeBtn.setOpacity(1);
        else likeBtn.setOpacity(0.5);

        retweet.setText(String.valueOf(reblogs));
        if (status.isReblogged()) retweetBtn.setOpacity(1);
        else retweetBtn.setOpacity(0.5);

        comment.setText(String.valueOf(status.getRepliesCount()));

        if (status.isBookmarked()) bookmarkBtn.setOpacity(1);
        else bookmarkBtn.setOpacity(0.5);

        setText(null);
        setGraphic(root);

    }

    @FXML
    private void onLikeBtn() {

        if (status.isFavourited()) {
            CompletableFuture.runAsync(this::unlike);
        } else {
            CompletableFuture.runAsync(this::like);
        }

    }

    private void like() {
        try {
            BusinessLogic.favouriteStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void unlike() {
        try {
            BusinessLogic.unfavouriteStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRetootBtn() {
        if (status.isReblogged()) {
            CompletableFuture.runAsync(this::unreblog);
        } else {
            CompletableFuture.runAsync(this::reblog);
        }
    }

    private void reblog() {
        try {
            BusinessLogic.reblogStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void unreblog() {
        try {
            BusinessLogic.unreblogStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the createdAt string of the status to a String to display in the toot date
     *
     * @param createdAt Attribute of the status
     * @return String to display in the toot date
     */
    private String getPrettyDate(String createdAt) {
        var creationDateTime = timeParser.parse(createdAt, OffsetDateTime::from);
        var now = OffsetDateTime.now();
        var timeSinceCreation = Duration.between(creationDateTime, now);

        if (timeSinceCreation.toSeconds() < 60)
            return (timeSinceCreation.getSeconds() + "s ago");
        else if (timeSinceCreation.toMinutes() < 60)
            return (timeSinceCreation.toMinutes() + "m ago");
        else if (timeSinceCreation.toHours() < 24)
            return (timeSinceCreation.toHours() + "h ago");
        else if (timeSinceCreation.toDays() < 7)
            return (timeSinceCreation.toDays() + "d ago");
        else if (creationDateTime.getYear() == now.getYear())
            return (timeFormatter.format(creationDateTime));
        else
            return (timeFormatterYear.format(creationDateTime));

    }
    /**
     * Method that bookmarks a status
     */
    @FXML
    void onBookmark() {
        if (status.isBookmarked()) {
            CompletableFuture.runAsync(this::unbookmark);
        }
        else {
            CompletableFuture.runAsync(this::bookmark);
        }

    }

    private void bookmark()  {
        try {
            BusinessLogic.bookmarkStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void unbookmark()  {
        try {
            BusinessLogic.unbookmarkStatus(status.getId());
            mainController.updateAccountTootsStatus(status.getId());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method to go to the profile of the account
     */
    @FXML
    public void goAccount() {
        mainController.setProfile(account);
    }

}
