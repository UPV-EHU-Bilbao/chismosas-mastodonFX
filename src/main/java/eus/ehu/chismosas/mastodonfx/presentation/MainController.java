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
     * Initialize the main controller
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

    @FXML
    void mouseFollowers() { sceneSwitch("Followers"); }

    @FXML
    void mouseFollowing() {
        sceneSwitch("Following");
    }

    @FXML
    void mouseProfile() {
        sceneSwitch("Toots");
    }

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
     * Show the list of statuses
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
     * Show the list of followings
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
     * Show the list of followers
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

    public void setProfile() {
        Account account = BusinessLogic.getAccount(userID);

        profilePic.setImage(new Image(account.getAvatar()));
        userNameLabel.setText("@"+account.getUsername());
        displayNameLabel.setText(account.getDisplayName());
    }



}
