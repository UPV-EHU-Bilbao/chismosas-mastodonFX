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
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AccountSelection {

    @FXML
    private ListView<Account> accountsList;

    @FXML
    private Button chooseAccountBtn;

    private Parent root;

    @FXML
    private void initialize() {
        chooseAccountBtn.disableProperty().bind(accountsList.getSelectionModel().selectedItemProperty().isNull());

        Set<Account> choosableAccounts = new HashSet<>();
        for (String id: BusinessLogic.getAccountLoginIds()) {
            try {
                choosableAccounts.add(BusinessLogic.getAccount(id));
            }
            catch (BigBoneRequestException e) {
                System.out.println("Error getting account " + id);
            }

        }

        accountsList.setCellFactory(param -> new AccountSelectionCell());
        accountsList.getItems().setAll(choosableAccounts);

    }

    @FXML
    private void chooseAccount() {
        var account = accountsList.getSelectionModel().getSelectedItem();

        BusinessLogic.login(account.getId());
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
