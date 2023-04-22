package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import static eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic.getAccount;


/**
 * This class is used to control the main view of the application
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainController {
    private static MainController instance;
    public static MainController getInstance() {return instance;}

    private final String userID = System.getenv("ID");

    private String id;

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

    @FXML
    private Button followBtn;




    private ListView<Status> tootListView;
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

        publishButton.disableProperty().bind(
                Bindings.isEmpty(newTootArea.textProperty())
        );


        updateTootListView(userID);
        updateFollowingListView(userID);
        updateFollowersListView(userID);
        mainPane.setCenter(tootListView);
        setProfile(userID);
    }

    /**
     * switches the scene to the toots list when the profile button is pressed
     */
    @FXML
    void mouseProfile() {
        sceneSwitch("Toots");
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





    /**
     * changes the main scene's center to the asked scene
     * @param scene the scene to be shown
     */
    private void sceneSwitch(String scene) {
        switch (scene) {
            case "Toots" -> {
                profileBtn.setEffect(dropShadow);
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);
                mainPane.setCenter(tootListView);
            }
            case "Following" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(dropShadow);
                followersBtn.setEffect(null);
                mainPane.setCenter(followingListView);
                updateFollowingListView(userID);
            }
            case "Followers" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(dropShadow);
                mainPane.setCenter(followersListView);
                updateFollowersListView(userID);
            }
        }
    }

    /**
     * gets the statuses of the user and shows them in a list
     */
    public void updateTootListView(String id) {
        try {
            var statusList = BusinessLogic.getStatuses(id);

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
            throw new RuntimeException("Error getting statuses, add your token to the environment variables", e);
        }
    }

    /**
     * Gets the list of accounts that the user is following and shows it
     */
    public void updateFollowingListView(String id) {
        try {
            var followingList = BusinessLogic.getFollowing(id);
            var following = FXCollections.observableList(followingList);
            followingListView.setItems(following);
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the list of accounts that are following the user and shows it
     */
    public void updateFollowersListView(String id) {
        try {
            var followersList = BusinessLogic.getFollowers(id);
            var followers = FXCollections.observableList(followersList);
            followersListView.setItems(followers);
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the profile picture, username and display name of the user
     */
    public void setProfile(String id) {
        try {
            var account = getAccount(id);
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
            updateTootListView(userID);
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
    /**
     * Changes the profile to the given id
     * @param id the id of the account to update
     */
    public void goProfile(String id) throws BigBoneRequestException {
        setProfile(id);
        updateTootListView(id);
        updateFollowersListView(id);
        updateFollowingListView(id);
        try {
            if(BusinessLogic.getRelationShip(id).get(0).isFollowing()){
                followBtn.setText("Unfollow");
            }
            else{
                followBtn.setText("Follow");
            }
        } catch (BigBoneRequestException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void followAccount(ActionEvent event) throws BigBoneRequestException {
        String id = StatusCell.getInstance().getAccountID();
        if(!BusinessLogic.getRelationShip(id).get(0).isFollowing()){
            BusinessLogic.followAccount(id);
            followBtn.setText("Unfollow");
        }
        else{
            BusinessLogic.unfollowAccount(id);
            followBtn.setText("Follow");
            MainController.getInstance().updateFollowingListView(id);
        }
    }

}
