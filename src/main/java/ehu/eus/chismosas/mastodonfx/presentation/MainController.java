package ehu.eus.chismosas.mastodonfx.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

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
    private ListView<?> tootList;

}
