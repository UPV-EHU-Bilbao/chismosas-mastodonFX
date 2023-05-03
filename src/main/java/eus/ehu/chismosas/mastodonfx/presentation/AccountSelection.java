package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
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

    private BooleanProperty validTokenEntered;
    private Account enteredAccount;


    @FXML
    private void initialize() {
        accountsList.setStyle("-fx-control-inner-background: #18181b");
        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());


        AddNewAccountBtn.disableProperty().bind(newID.textProperty().isEmpty().or(newToken.textProperty().isEmpty()));
        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());

        validTokenEntered = new SimpleBooleanProperty(false);
        AddNewAccountBtn.disableProperty().bind(validTokenEntered.not());
    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();
        MainApplication.login(account);
    }

    @FXML
    void addNewAccount() {
        if (validTokenEntered.get()) {
            BusinessLogic.addAccountLogin(enteredAccount.getId(), newToken.getText());
        }
    }

    void verifyToken() {
        CompletableFuture.runAsync(() -> {
            var account = BusinessLogic.verifyCredentials(newToken.getText());
            if (account != null) {
                validTokenEntered.set(true);
                showValidToken(account);
            }
            else showInvalidToken();
        });
    }

    private void showValidToken(Account account) {
        System.out.println("Valid token for account " + account.getId());
    }

    private void showInvalidToken() {
        System.out.println("Invalid token.");
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
