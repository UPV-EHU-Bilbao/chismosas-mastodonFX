package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import eus.ehu.chismosas.mastodonfx.persistance.DBManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import social.bigbone.api.entity.Account;

import java.io.IOException;

public class AccountSelection{

    public static AccountSelection instance;

    public static AccountSelection getInstance() {
        return instance;
    }

    @FXML
    private Pane loginPane;

    @FXML
    private ListView<Account> accountsList;

    @FXML
    private Button chooseAccountBtn;

    @FXML
    private Button AddNewAccountBtn;

    @FXML
    private TextField newID;

    @FXML
    private TextField newToken;

    private Parent root;

    private String lightTheme = getClass().getResource("/eus/ehu/chismosas/mastodonfx/presentation/styles/light.css").toExternalForm();
    private String darkTheme = getClass().getResource("/eus/ehu/chismosas/mastodonfx/presentation/styles/dark.css").toExternalForm();

    @FXML
    private void initialize() {
        instance = this;
        setDarkTheme();

        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());
        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());

        AddNewAccountBtn.disableProperty().bind(
                newID.textProperty().isEmpty().or(newToken.textProperty().isEmpty())
        );

    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();
        MainApplication.login(account);
    }

    @FXML
    void addNewAccount() {
        String id = newID.getText();
        String token = newToken.getText();
        try {
           BusinessLogic.addAccountLogin(id, token);
            accountsList.getItems().add(BusinessLogic.getAccount(id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * This method is called when the user clicks on the "Light" button.
     * It changes the theme of the account selection screen to light.
     */
    public void setLightTheme() {
        loginPane.getStylesheets().clear();
        loginPane.getStylesheets().add(lightTheme);
    }

    /**
     * This method is called when the user clicks on the "Dark" button.
     * It changes the theme of the account selection screen to dark.
     */
    public void setDarkTheme() {
        loginPane.getStylesheets().clear();
        loginPane.getStylesheets().add(darkTheme);
    }

    public class AccountSelectionCell extends ListCell<Account> {
        private Parent root;

        @FXML
        private ImageView avatar;

        @FXML
        private Label userName;

        @Override
        protected void updateItem(Account item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            if (root == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("account-selection-cell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                    root = loader.getRoot();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            root.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2)
                    chooseAccount();
            });

            setText(null);
            setGraphic(root);

            userName.setText("@" + item.getUsername());
            avatar.setImage(ImageCache.get(item.getAvatar()));

        }
    }
}
