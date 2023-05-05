package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import social.bigbone.api.entity.Account;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AccountSelection {


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


    private Account enteredAccount;


    @FXML
    private void initialize() {
        accountsList.setStyle("-fx-control-inner-background: #18181b");
        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());

        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());



        newToken.textProperty().addListener((observable, oldValue, newValue) -> {
            AddNewAccountBtn.setDisable(true);

            newValue = newValue.trim();
            newToken.setText(newValue);

            if (newValue.length() == 43) verifyToken();
        });

        CompletableFuture.runAsync(this::validateStored);
    }

    private void validateStored() {
        BusinessLogic.validateStoredAccounts();
        var loggable = BusinessLogic.getLoggableAccounts();
        var items = accountsList.getItems();
        Platform.runLater(() -> items.setAll(loggable));
    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();
        MainApplication.login(account);
    }

    @FXML
    void addNewAccount() {
        BusinessLogic.addAccountLogin(enteredAccount.getId(), newToken.getText());
        newToken.clear();
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());
    }

    void verifyToken() {
        if (BusinessLogic.isTokenInDB(newToken.getText())) {
            showTokenAlreadyStored();
            return;
        }
        enteredAccount = BusinessLogic.verifyCredentials(newToken.getText());
        if (enteredAccount != null) {
            showValidToken();
            AddNewAccountBtn.setDisable(false);
        }
        else {
            showInvalidToken();
        }
    }


    private void showValidToken() {
        System.out.println("Valid token for account " + enteredAccount.getId());
    }

    private void showInvalidToken() {
        System.out.println("Invalid token.");
    }

    private void showTokenAlreadyStored() {
        System.out.println("Token is already stored.");
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
