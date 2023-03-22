package eus.ehu.chismosas.mastodonfx.presentation;

import ehu.eus.chismosas.mastodonfx.businesslogic.BusinessLogic;
import ehu.eus.chismosas.mastodonfx.businesslogic.StatusCell;
import ehu.eus.chismosas.mastodonfx.domain.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.Node;


import java.io.IOException;
import java.util.List;

public class MainController {
    private final String userID = System.getenv("ID");

    @FXML
    private Button bookmarksBtn;

    @FXML
    private Pane contentPane;

    @FXML
    private Button exploreBtn;

    @FXML
    private Button favsBtn;

    @FXML
    private Button msgBtn;

    @FXML
    private TextArea newToot;

    @FXML
    private Button notificationsBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private TextField searcher;

    @FXML
    private Pane titlePane;

    @FXML
    private ListView<Status> tootList;

    private FXMLLoader loader;

    @FXML
    void initialize() {
        /*List<Status> statusList = Utils.getStatusList("109897298389421503");
        webEngine = content.getEngine();
        webEngine.loadContent(statusList.get(0).content);
        author.setText(statusList.get(0).account.username);
        date.setText(statusList.get(0).created_at);*/

        //Status status = BusinessLogic.getStatuses("109897298389421503").get(0);
        showList();
    }

    public void showList(){
        List<Status> statusList = BusinessLogic.getStatuses("109897298389421503");

        ObservableList<Status> items = FXCollections.observableArrayList(statusList);

        if (tootList != null) {
            tootList.setItems(items);
            tootList.setCellFactory(param -> {
                var cell = new StatusCell();
                //cell.setOnMousecClicked((evt) -> {
                //Status status = cell.getItem();
                //if (status != null) System.out.println("Status's data: " + status);

            //});

                return cell;


            });
        }

        tootList.getItems().addAll(statusList);
    }


}
