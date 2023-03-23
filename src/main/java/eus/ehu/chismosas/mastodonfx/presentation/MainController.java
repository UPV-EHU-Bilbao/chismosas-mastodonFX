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

    private ListView<Account> accountListView;

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
                //cell.setOnMousecClicked((evt) -> {
               // Status status = cell.getItem();
               // if (status != null) System.out.println("Status's data: " + status);

          //

                return cell;


            });
        }

        tootListView.getItems().addAll(statusList);
    }

    public void showFollowignList(){

        try {
             List<Account> accountList = BusinessLogic.getFollowing(userID);
             ObservableList<Account> following = FXCollections.observableList(accountList);
        }
        catch (BigBoneRequestException e){
            throw new RuntimeException(e);
        }

        if ()


    }



}
