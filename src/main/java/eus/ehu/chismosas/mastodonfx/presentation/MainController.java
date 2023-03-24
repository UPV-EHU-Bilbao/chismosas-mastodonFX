package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.util.List;

import javafx.scene.control.ListView;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.entity.*;
import social.bigbone.api.exception.BigBoneRequestException;

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
     * @return void
     */
    @FXML
    void initialize() {
        tootListView = new ListView<>();
        followersListView = new ListView<>();
        followingListView = new ListView<>();
        dropShadow = new DropShadow();
        showStatusList();
        showFollowersList();
        showFollowingList();
        mainPane.setCenter(tootListView);
        setProfile();
        dropShadow = new DropShadow();

    }

    @FXML
    void mouseFollowers(ActionEvent event) { sceneSwitch("Followers"); }

    @FXML
    void mouseFollowing(ActionEvent event) {
        sceneSwitch("Following");
    }

    @FXML
    void mouseProfile(MouseEvent event) {
        sceneSwitch("Toots");
    }

    private void sceneSwitch(String scene){
        switch (scene){
            case "Followers": mainPane.setCenter(followersListView);
            followersBtn.setEffect(dropShadow);
            followingBtn.setEffect(null);
            profileBtn.setEffect(null);
            break;
            case "Following": mainPane.setCenter(followingListView);
            followingBtn.setEffect(dropShadow);
            followersBtn.setEffect(null);
            profileBtn.setEffect(null);
            break;
            case "Toots": mainPane.setCenter(tootListView);
            profileBtn.setEffect(dropShadow);
            followersBtn.setEffect(null);
            followingBtn.setEffect(null);
            break;
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
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }

        ObservableList<Status> items = FXCollections.observableArrayList(statusList);

        if (tootListView != null) {
            tootListView.setItems(items);

            tootListView.setCellFactory(param -> {
                var cell = new StatusCell();
                return cell;
            });
        }

        tootListView.getItems().addAll(statusList);
    }
    /**
     * Show the list of followings
     */
    public void showFollowingList(){

        try {
           List<Account> accountList = BusinessLogic.getFollowing(userID);
           ObservableList<Account> following = FXCollections.observableList(accountList);
            followingListView.setItems(following);
            followingListView.setCellFactory(param -> {
                var cell = new AccountCell();
                return cell;
            });
        }
        catch (BigBoneRequestException e){
            System.out.println("Could not get followings");
        }
    }

    /**
     * Show the list of followers
     * @return void
     */
    public void showFollowersList(){

        try {
            List<Account> accountList = BusinessLogic.getFollowers(userID);
            ObservableList<Account> followers = FXCollections.observableList(accountList);
            followersListView.setItems(followers);
            followersListView.setCellFactory(param -> {
                var cell = new AccountCell();
                return cell;
            });
        }
        catch (BigBoneRequestException e) {
            System.out.println("Could not get followers");;
        }
    }

    public void setProfile() {
        Account account = BusinessLogic.getAccount(userID);

        profilePic.setImage(new Image(account.getAvatar()));
        userNameLabel.setText(account.getUsername());
        displayNameLabel.setText(account.getDisplayName());
    }



}
