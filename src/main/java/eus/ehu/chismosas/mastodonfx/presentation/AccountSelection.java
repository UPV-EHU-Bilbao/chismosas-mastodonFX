package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import social.bigbone.api.entity.Account;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static eus.ehu.chismosas.mastodonfx.presentation.MainApplication.t;

public class AccountSelection {

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
    private Button removeAccountBtn;
    @FXML
    private TextField tokenField;
    @FXML
    private Label tokenStatusLabel;
    @FXML
    private Button AddAccountBtn;

    private Account enteredAccount;

    private Alert a = new Alert(Alert.AlertType.NONE);



    @FXML
    private void initialize() {

        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());
        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());

        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());
        removeAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());

        tokenStatusLabel.setVisible(false);
        AddAccountBtn.setDisable(true);
        tokenField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.trim();
            tokenField.setText(newValue);

            if (newValue.length() == 43)
                verifyToken();
            else {
                tokenStatusLabel.setVisible(false);
                AddAccountBtn.setDisable(true);
            }
        });

    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();
        if (BusinessLogic.verifyAccountCredentials(account))
            MainApplication.login(account);
        else {
            System.out.println("Invalid account!");
            accountsList.getItems().remove(account);
            CompletableFuture.runAsync(() -> BusinessLogic.removeAccountLogin(account.getId()));
        }
    }

    @FXML
    private void removeAccount() {
        Account account = accountsList.getSelectionModel().getSelectedItem();
        CompletableFuture.runAsync(() -> BusinessLogic.removeAccountLogin(account.getId()));
        accountsList.getItems().remove(account);
    }

    @FXML
    private void addAccount() {
        BusinessLogic.addAccountLogin(enteredAccount.getId(), tokenField.getText());
        tokenField.clear();
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());
    }


    private void verifyToken() {
        if (BusinessLogic.isTokenStored(tokenField.getText())) {
            showTokenAlreadyStored();
            return;
        }

        enteredAccount = BusinessLogic.verifyCredentials(tokenField.getText());
        if (enteredAccount == null) showInvalidToken();
        else {
            showValidToken();
            AddAccountBtn.setDisable(false);
        }
    }

    private void showValidToken() {
        tokenStatusLabel.setText("Valid token for account @" + enteredAccount.getUsername());
        tokenStatusLabel.setVisible(true);
    }

    private void showInvalidToken() {
        tokenStatusLabel.setText("Invalid token");
        tokenStatusLabel.setVisible(true);
    }

    private void showTokenAlreadyStored() {
        tokenStatusLabel.setText("Token is already stored");
        tokenStatusLabel.setVisible(true);
    }



    public class AccountSelectionCell extends ListCell<Account> {
        private Parent root;

        @FXML
        private ImageView avatar;

        @FXML
        private Label username;

        @Override
        protected void updateItem(Account item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            if (root == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("account-selection-cell.fxml"), t("eus_ES"));
                loader.setController(this);
                try {
                    loader.load();
                    root = loader.getRoot();
                } catch (IOException e) {
                    // set alert type
                    a.setAlertType(Alert.AlertType.ERROR);
                    // set content text
                    a.setContentText("Failed to update item");
                    // show the dialog
                    a.show();
                }
            }

            root.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2)
                    chooseAccount();
            });

            setText(null);
            setGraphic(root);

            username.setText("@" + item.getUsername());
            avatar.setImage(ImageCache.get(item.getAvatar()));

        }
    }
}
