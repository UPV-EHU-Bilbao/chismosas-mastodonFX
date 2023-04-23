package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.concurrent.CompletableFuture;

/**
 * This class is used to control the main view of the application
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainController {
    private static MainController instance;
    public static MainController getInstance() {return instance;}

    private final String userID = BusinessLogic.getUserId();

    @FXML
    private ToolBar bookmarksBtn;

    @FXML
    private Label displayNameLabel;

    @FXML
    private ToolBar exploreBtn;

    @FXML
    private ToolBar favsBtn;

    @FXML
    private ToolBar favsBtn1;

    @FXML
    private Button followersBtn;

    @FXML
    private Button followingBtn;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ToolBar msgBtn;

    @FXML
    private TextArea newTootArea;

    @FXML
    private ToolBar notificationsBtn;

    @FXML
    private ToolBar profileBtn;

    @FXML
    private ImageView profilePic;

    @FXML
    private TextField searcher;

    @FXML
    private ToolBar settingsBtn;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button publishButton;


    private ListView<Status> tootListView;
    private Pageable<Status> homeTimeline;
    private ListView<Account> followersListView;
    private ListView<Account> followingListView;
    private DropShadow dropShadow;

    /**
     * Initialize the main controller by setting the toots in a list
     */
    @FXML
    void initialize() {
        instance = this;

        tootListView = new ListView<>();
        tootListView.setCellFactory(param -> new StatusCell());
        followingListView = new ListView<>();
        followingListView.setCellFactory(param -> new AccountCell());
        followersListView = new ListView<>();
        followersListView.setCellFactory(param -> new AccountCell());
        dropShadow = new DropShadow();

        tootListView.setStyle("-fx-control-inner-background: #18181b");
        followingListView.setStyle("-fx-control-inner-background: #18181b");
        followersListView.setStyle("-fx-control-inner-background: #18181b");

        publishButton.disableProperty().bind((newTootArea.textProperty().isEmpty()));

        showAccountToots();
        updateFollowingListView();
        updateFollowersListView();
        mainPane.setCenter(tootListView);
        setProfile();

        CompletableFuture.runAsync(() -> {
            try {
                homeTimeline = BusinessLogic.getHomeTimeline();
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * switches the scene to the toots list when the profile button is pressed
     */
    @FXML
    void mouseProfile() {
        sceneSwitch("Profile");
    }

    /**
     * switches the scene to the following list when the following button is pressed
     */
    @FXML
    void mouseFollowing() {
        sceneSwitch("Following");
    }

    /**
     * switches the scene to the followers list when the followers button is pressed
     */
    @FXML
    void mouseFollowers() {
        sceneSwitch("Followers");
    }

    @FXML
    void mouseHome() {
        sceneSwitch("HomeTimeline");
    }

    /**
     * changes the main scene's center to the asked scene
     * @param scene the scene to be shown
     */
    private void sceneSwitch(String scene) {
        switch (scene) {
            case "Profile" -> {
                profileBtn.setEffect(dropShadow);
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);

                showAccountToots();
                mainPane.setCenter(tootListView);
            }
            case "HomeTimeline" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);

                showHometimeline();
                mainPane.setCenter(tootListView);
            }
            case "Following" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(dropShadow);
                followersBtn.setEffect(null);
                mainPane.setCenter(followingListView);
            }
            case "Followers" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(dropShadow);
                mainPane.setCenter(followersListView);
            }
        }
    }

    /**
     * gets the statuses of the user and shows them in a list
     */
    public void showAccountToots() {
        try {
            var statusList = BusinessLogic.getStatuses(userID);

            // Process reblogs and filters out toots that we cannot display yet
            var statusIterator = statusList.listIterator();
            while (statusIterator.hasNext()) {
                Status status = statusIterator.next();

                if (status.getReblog() != null) {
                    status = status.getReblog();
                    statusIterator.set(status);
                }
                if (status.getContent().equals("")) {
                    statusIterator.remove();
                }
            }

            var items = FXCollections.observableArrayList(statusList);
            tootListView.setItems(items);
        }
        catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    public void showHometimeline() {
        var items = FXCollections.observableArrayList(homeTimeline.getPart());
        tootListView.setItems(items);
    }

    /**
     * Gets the list of accounts that the user is following and shows it
     */
    public void updateFollowingListView() {
        try {
            var followingList = BusinessLogic.getFollowing(userID);
            var following = FXCollections.observableList(followingList);
            followingListView.setItems(following);
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the list of accounts that are following the user and shows it
     */
    public void updateFollowersListView() {
        try {
            var followersList = BusinessLogic.getFollowers(userID);
            var followers = FXCollections.observableList(followersList);
            followersListView.setItems(followers);
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the profile picture, username and display name of the user
     */
    public void setProfile() {
        try {
            var account = BusinessLogic.getAccount(userID);
            profilePic.setImage(ImageCache.get(account.getAvatar()));
            userNameLabel.setText("@" + account.getUsername());
            displayNameLabel.setText(account.getDisplayName());
        }
        catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Publishes the toot written in the text area
     */
    public void publishToot() {
        String toot = newTootArea.getText();
        try {
            BusinessLogic.postStatus(toot);
            newTootArea.clear();
            showAccountToots();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Requests the status with the given id and updates it in the list
     * @param id the id of the status to update
     */
    public void updateStatus(String id) {
        var iterator = tootListView.getItems().listIterator();
        while (iterator.hasNext()) {
            var status = iterator.next();
            if (status.getId().equals(id)) {
                try {
                    var updatedStatus = BusinessLogic.getStatus(id);
                    iterator.set(updatedStatus);
                } catch (BigBoneRequestException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
