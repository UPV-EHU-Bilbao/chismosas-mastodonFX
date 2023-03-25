package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.List;

/**
 * This class is used to control the main view of the application
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainController {
    private final String userID = System.getenv("ID");

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
    private TextArea newToot;

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

    private FXMLLoader loader;

    private ListView<Status> tootListView;

    private ListView<Account> followersListView;
    private ListView<Account> followingListView;

    private DropShadow dropShadow;

    /**
     * Initialize the main controller by setting the toots in a list
     */
    @FXML
    void initialize() {
        tootListView = new ListView<>();
        followersListView = new ListView<>();
        followingListView = new ListView<>();
        dropShadow = new DropShadow();


        tootListView.setStyle("-fx-control-inner-background: #18181b");
        followersListView.setStyle("-fx-control-inner-background: #18181b");
        followingListView.setStyle("-fx-control-inner-background: #18181b");

        showStatusList();
        showFollowersList();
        showFollowingList();
        mainPane.setCenter(tootListView);
        setProfile();
    }

    /**
     * switches the scene to the followers list when the followers button is pressed
     */
    @FXML
    void mouseFollowers() { sceneSwitch("Followers"); }

    /**
     * switches the scene to the following list when the following button is pressed
     */
    @FXML
    void mouseFollowing() {
        sceneSwitch("Following");
    }

    /**
     * switches the scene to the toots list when the profile button is pressed
     */
    @FXML
    void mouseProfile() {
        sceneSwitch("Toots");
    }

    /**
     * changes the main scene's center to the asked scene
     * @param scene the scene to be shown
     */
    private void sceneSwitch(String scene){
        switch (scene) {
            case "Followers" -> {
                mainPane.setCenter(followersListView);
                followersBtn.setEffect(dropShadow);
                followingBtn.setEffect(null);
                profileBtn.setEffect(null);
            }
            case "Following" -> {
                mainPane.setCenter(followingListView);
                followingBtn.setEffect(dropShadow);
                followersBtn.setEffect(null);
                profileBtn.setEffect(null);
            }
            case "Toots" -> {
                mainPane.setCenter(tootListView);
                profileBtn.setEffect(dropShadow);
                followersBtn.setEffect(null);
                followingBtn.setEffect(null);
            }
        }
    }
    /**
     * gets the statuses of the user and shows them in a list
     * @return void
     */
    public void showStatusList(){
        List<Status> statusList;
        try {
            statusList = BusinessLogic.getStatuses(userID);

            // Process reblogs and filters out toots that we cannot display yet
            var statusIterator = statusList.listIterator();
            while(statusIterator.hasNext()) {
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
            tootListView.setCellFactory(param -> new StatusCell());
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Gets the list of accounts that the user is following and shows it
     */
    public void showFollowingList(){

        try {
           List<Account> accountList = BusinessLogic.getFollowing(userID);
           ObservableList<Account> following = FXCollections.observableList(accountList);
            followingListView.setItems(following);
            followingListView.setCellFactory(param -> new AccountCell());
        }
        catch (BigBoneRequestException e){
            System.out.println("Could not get followings");
        }
    }

    /**
     * Gets the list of accounts that are following the user and shows it
     */
    public void showFollowersList(){

        try {
            List<Account> accountList = BusinessLogic.getFollowers(userID);
            ObservableList<Account> followers = FXCollections.observableList(accountList);
            followersListView.setItems(followers);
            followersListView.setCellFactory(param -> new AccountCell());
        }
        catch (BigBoneRequestException e) {
            System.out.println("Could not get followers");
        }
    }

    /**
     * Sets the profile picture, username and display name of the user
     */
    public void setProfile() {
        Account account = BusinessLogic.getAccount(userID);

        profilePic.setImage(new Image(account.getAvatar()));
        userNameLabel.setText("@"+account.getUsername());
        displayNameLabel.setText(account.getDisplayName());
    }



}
