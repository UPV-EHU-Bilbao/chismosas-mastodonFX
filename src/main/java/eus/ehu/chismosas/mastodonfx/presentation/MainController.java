package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
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
    private String currentID;

    @FXML
    private ToolBar bookmarksBtn;

    @FXML
    private Label displayNameLabel;

    @FXML
    private ToolBar exploreBtn;

    @FXML
    private ToolBar favsBtn;

    @FXML
    private ToolBar homeBtn;

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

    @FXML
    private Button followBtn;




    private ListView<Status> tootListView;
    private Pageable<Status> homeTimeline;
    private ListView<Account> followersListView;
    private ListView<Account> followingListView;

    private Scene settingsScene;
    private DropShadow dropShadow;

    /**
     * Initialize the main controller by setting the toots in a list
     */
    @FXML
    void initialize(){
        instance = this;
        tootListView = new ListView<>();
        tootListView.setCellFactory(param -> new StatusCell());
        followingListView = new ListView<>();
        followingListView.setCellFactory(param -> new AccountCell());
        followersListView = new ListView<>();
        followersListView.setCellFactory(param -> new AccountCell());
        dropShadow = new DropShadow();

        publishButton.disableProperty().bind((newTootArea.textProperty().isEmpty()));

        currentID = userID;
        updateBanner();
        showAccountToots();
        mainPane.setCenter(tootListView);
        settingsSceneLoader();

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
        showProfile(userID);
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
    void mouseSettings() { sceneSwitch("Settings");}

    @FXML
    void mouseHome() {
        sceneSwitch("HomeTimeline");
    }

    /**
     * changes the main scene's center to the asked scene
     * @param scene the scene to be shown
     */
    private void sceneSwitch(String scene) {
        selectBtn(scene);
        var settingsRoot = settingsScene.getRoot();
        switch (scene) {
            case "Profile" -> {

                showAccountToots();
                mainPane.setCenter(tootListView);
            }
            case "HomeTimeline" -> {

                showHometimeline();
                mainPane.setCenter(tootListView);
            }
            case "Following" -> {

                mainPane.setCenter(followingListView);
                updateFollowingListView();
            }
            case "Followers" -> {
                mainPane.setCenter(followersListView);
                updateFollowersListView();
            }
            case "Settings" -> {
                mainPane.setCenter(settingsRoot);
                //BUG: If I don't update the pane the settings window doesn't show up
                mainPane.setCenter(followersListView);
                mainPane.setCenter(settingsRoot);
            }

        }
    }

    /**
     * This method sets the style of the button that has just been pressed
     * so it can diffrentiate it from the rest
     * @param btn is the selected button
     */
    public void selectBtn(String btn){
        if(btn.equals("Profile")) profileBtn.setId("selected");
        else profileBtn.setId("");
        if(btn.equals("HomeTimeline")) homeBtn.setId("selected");
        else homeBtn.setId("");
        if(btn.equals("Following")) followingBtn.setId("selected");
        else followingBtn.setId("");
        if(btn.equals("Followers")) followersBtn.setId("selected");
        else followersBtn.setId("");
        if(btn.equals("Settings")) settingsBtn.setId("selected");
        else settingsBtn.setId("");

    }

    /**
     * gets the statuses of the user and shows them in a list
     */
    public void showAccountToots() {
        try {
            var statusList = BusinessLogic.getStatuses(currentID);

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
            var followingList = BusinessLogic.getFollowing(currentID);
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
            var followersList = BusinessLogic.getFollowers(currentID);
            var followers = FXCollections.observableList(followersList);
            followersListView.setItems(followers);
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the profile picture, username and display name of the user
     */
    public void updateBanner() {
        try {
            var account = BusinessLogic.getAccount(currentID);
            profilePic.setImage(ImageCache.get(account.getAvatar()));
            userNameLabel.setText("@" + account.getUsername());
            displayNameLabel.setText(account.getDisplayName());
            showFollowButton();
        }
        catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Shows the follow button if the user is not in their own profile
     */
    public void showFollowButton() {
        if (userID.equals(currentID)) {
            followBtn.setVisible(false);
        } else {
            try {
                if(BusinessLogic.getRelationship(currentID).isFollowing()){
                    followBtn.setText("Unfollow");
                }
                else{
                    followBtn.setText("Follow");
                }
                followBtn.setVisible(true);
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
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
    public void settingsSceneLoader() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
            settingsScene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the profile to the given id
     * @param id the id of the account to update
     */
    public void showProfile(String id) {

        currentID = id;
        updateBanner();
        sceneSwitch("Profile");

    }
    @FXML
    void followAccount() {
        try {
            if(!BusinessLogic.getRelationship(currentID).isFollowing()){
                BusinessLogic.followAccount(currentID);
                followBtn.setText("Unfollow");
            }
            else{
                BusinessLogic.unfollowAccount(currentID);
                followBtn.setText("Follow");
            }
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

}
