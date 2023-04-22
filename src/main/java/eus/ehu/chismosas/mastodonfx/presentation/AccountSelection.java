package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import social.bigbone.api.entity.Account;

import java.io.IOException;

public class AccountSelection {

    @FXML
    private ListView<Account> accountsList;

    @FXML
    private Button chooseAccountBtn;

    private Parent root;

    @FXML
    private void initialize() {
        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());

        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(BusinessLogic.getLoggableAccounts());

    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();

        BusinessLogic.login(account);
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            MainApplication.setScene(scene);
            MainApplication.setTitle("MastodonFX - @" + account.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class AccountSelectionCell extends ListCell<Account> {
        private Parent root;

        @Override
        protected void updateItem(Account item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            if (root == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
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
            }); //TODO: Test if account is selected in the list when double clicking

            setText(null);
            setGraphic(root);

        }
    }
}
