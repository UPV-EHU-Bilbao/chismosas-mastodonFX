package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.util.List;

import javafx.scene.control.ListView;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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

    @FXML
    void initialize() {
        /*List<Status> statusList = Utils.getStatusList("109897298389421503");
        webEngine = content.getEngine();
        webEngine.loadContent(statusList.get(0).content);
        author.setText(statusList.get(0).account.username);
        date.setText(statusList.get(0).created_at);*/

        //Status status = BusinessLogic.getStatuses("109897298389421503").get(0);
        //showList();
    }

    public void showStatusList(){
        List<Status> statusList = null;
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

    public void showFollowignList(){
        List<Account> accountList = null;
        try {
             accountList = BusinessLogic.getFollowing(userID);

        }
        catch (BigBoneRequestException e){
            throw new RuntimeException(e);
        }
        ObservableList<Account> following = FXCollections.observableList(accountList);

        if (followingListView != null){
            followingListView.setItems(following);
            followingListView.setCellFactory(param -> {
                var cell = new AccountCell();
                return cell;
            });
        }

        followingListView.getItems().addAll(accountList);

    }

    public void showFollowersList(){
        List<Account> accountList = null;
        try {
            accountList = BusinessLogic.getFollowers(userID);

        }
        catch (BigBoneRequestException e){
            throw new RuntimeException(e);
        }
        ObservableList<Account> followers = FXCollections.observableList(accountList);

        if (followersListView != null){
            followersListView.setItems(followers);
            followersListView.setCellFactory(param -> {
                var cell = new AccountCell();
                return cell;
            });
        }

        followersListView.getItems().addAll(accountList);

    }

}
