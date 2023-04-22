package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import social.bigbone.api.entity.Account;
import social.bigbone.api.exception.BigBoneRequestException;

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

    private FXMLLoader loader;


    /**
     * Updates the account to be shown in the list
     *
     * @param item  account to add to the list
     * @param empty if the list is empty
     */
    @Override
    protected void updateItem(Account item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("account.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        displayName.setText(item.getDisplayName());
        userName.setText("@" + item.getUsername());
        avatar.setImage(ImageCache.get(item.getAvatar()));

        setText(null);
        setGraphic(loader.getRoot());

        try {
        if(BusinessLogic.getRelationShip(getItem().getId()).get(0).isFollowing()){
            followBtn.setText("Unfollow");
        }
        else{
            followBtn.setText("Follow");
        }
    } catch (BigBoneRequestException e) {
        e.printStackTrace();
    }
    }

    /**
     * Follow the account on which the button is clicked
     * @param event .
     */
    @FXML
    void followAccount(ActionEvent event) {
        String id = getItem().getId();
        try {
            if(!BusinessLogic.getRelationShip(getItem().getId()).get(0).isFollowing()){
                BusinessLogic.followAccount(id);
                followBtn.setText("Unfollow");
            }
            else{
                BusinessLogic.unfollowAccount(id);
                followBtn.setText("Follow");
                MainController.getInstance().updateFollowingListView();
            }

        } catch (BigBoneRequestException e) {
            e.printStackTrace();
        }
    }


}
