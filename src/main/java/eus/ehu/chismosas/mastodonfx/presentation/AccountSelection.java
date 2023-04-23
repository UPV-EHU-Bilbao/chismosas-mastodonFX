package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import eus.ehu.chismosas.mastodonfx.persistance.DBManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Application;

import java.io.IOException;

public class AccountSelection{


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

    @FXML
    private void initialize() {
        accountsList.setStyle("-fx-control-inner-background: #18181b");
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

    @FXML
    void addNewAccount() {
        String id = newID.getText();
        String token = newToken.getText();
        try {
            DBManager.storeAccount(id, token);
            accountsList.getItems().add(BusinessLogic.getAccount(id));
            System.out.println("Account added");
        }catch (Exception e){
            System.out.println("Couldn't add the account");
            e.printStackTrace();
        }
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
            }); //TODO: Test if account is selected in the list when double clicking

            setText(null);
            setGraphic(root);

            userName.setText("@" + item.getUsername());
            avatar.setImage(ImageCache.get(item.getAvatar()));

        }
    }
}
