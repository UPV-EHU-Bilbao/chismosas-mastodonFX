package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import eus.ehu.chismosas.mastodonfx.businesslogic.RelationshipCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import social.bigbone.api.entity.Account;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;

import static eus.ehu.chismosas.mastodonfx.presentation.MainApplication.t;

/**
 * This class is used to update and show the account information
 * in the list that will be shown in the main view when button 'following'
 * or 'followers' are pressed
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class AccountCell extends ListCell<Account> {


    @FXML
    private ImageView avatar;
    @FXML
    private Label content;
    @FXML
    private Label date;
    @FXML
    private Label displayName;
    @FXML
    private Label userName;
    @FXML
    private Button followBtn;
    private Account account;
    private Parent root;

    private Alert a = new Alert(Alert.AlertType.NONE);

    /**
     * Updates the account to be shown in the list
     *
     * @param item  account to add to the list
     * @param empty if the list is empty
     */
    @Override
    protected void updateItem(Account item, boolean empty) {
        account = item;

        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"), t("eus_ES"));
            loader.setController(this);
            try {
                root = loader.load();
            } catch (IOException e) {
                // set alert type
                a.setAlertType(Alert.AlertType.ERROR);
                // set content text
                a.setContentText("Failed to update item");
                // show the dialog
                a.show();
            }
        }

        displayName.setText(account.getDisplayName());
        userName.setText("@" + account.getUsername());
        avatar.setImage(ImageCache.get(account.getAvatar()));

        if (account.getId().equals(BusinessLogic.getUserId())) followBtn.setVisible(false);
        else {
            if (RelationshipCache.get(account).isFollowing())
                followBtn.setText(MainController.getInstance().showUnfollow());
            else
                followBtn.setText(MainController.getInstance().showFollow());

            followBtn.setVisible(true);
        }

        setText(null);
        setGraphic(root);

    }

    /**
     * Follow the account on which the button is clicked
     */
    @FXML
    void followAccount() {
        MainController mc = MainController.getInstance();

        if (!RelationshipCache.get(account).isFollowing()) {
            mc.followAccount(account);
            followBtn.setText(MainController.getInstance().showUnfollow());
        } else {
            mc.unfollowAccount(account);
            followBtn.setText(MainController.getInstance().showFollow());
        }
    }

    /**
     * Method to go to the profile of the account
     */
    @FXML
    public void goAccount() {
        MainController.getInstance().setProfile(account);
    }

}
