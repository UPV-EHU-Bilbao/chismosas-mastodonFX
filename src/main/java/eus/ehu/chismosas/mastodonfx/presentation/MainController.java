package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import eus.ehu.chismosas.mastodonfx.businesslogic.RelationshipCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static eus.ehu.chismosas.mastodonfx.presentation.MainApplication.t;

/**
 * This class is used to control the main view of the application
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainController {
    private final DropShadow dropShadow = new DropShadow();
    @FXML
    private ToolBar bookmarksBtn;
    @FXML
    private Label displayNameLabel;
    @FXML
    private ToolBar exploreBtn;
    @FXML
    private ToolBar favsBtn;
    @FXML
    private ToolBar favsBtn1;
    @FXML
    private ToolBar homeBtn;

    @FXML
    private Button followersBtn;
    @FXML
    private Button followingBtn;
    @FXML
    private BorderPane mainPane;
    @FXML
    private ToolBar msgBtn;
    @FXML
    private TextArea newTootArea;
    @FXML
    private ToolBar notificationsBtn;
    @FXML
    private ToolBar profileBtn;
    @FXML
    private ImageView profilePic;
    @FXML
    private TextField searcher;
    @FXML
    private ToolBar settingsBtn;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button postButton;
    @FXML
    private Button followBtn;

    private static MainController instance;
    private Account userAccount;
    private Account currentAccount;
    private Scene settingsScene;
    private ListView<Status> tootListView;
    private ListView<Account> accountListView;
    private List<Status> accountToots;

    private List<Status> favouritedToots;
    private Future<List<Account>> followersList;
    private Future<List<Account>> followingList;
    private Future<List<Status>> homeTimeline;
    private String currentView;


    public static MainController getInstance() {return instance;}

    /**
     * Initialize the main controller by setting the toots in a list
     */
    @FXML
    void initialize() {
        instance = this;
        userAccount = BusinessLogic.getUserAccount();

        tootListView = new ListView<>();
        tootListView.setCellFactory(param -> new StatusCell());
        accountListView = new ListView<>();
        accountListView.setCellFactory(param -> new AccountCell());

        postButton.disableProperty().bind((newTootArea.textProperty().isEmpty()));

        loadSettingsScene();
        updateHomeTimeline();
        setProfile(userAccount);
    }

    /**
     * switches the scene to the toots list when the profile button is pressed
     */
    @FXML
    void mouseProfile() {
        setProfile(userAccount);
    }

    @FXML
    void mouseFollowers() {
        switchView("Followers");
    }

    /**
     * switches the scene to the following list when the following button is pressed
     */
    @FXML
    void mouseFollowing() {
        switchView("Following");
    }

    /**
     * switches the scene to the settings view when the settings button is pressed
     */
    @FXML
    void mouseSettings() {
        switchView("Settings");
    }
    /**
     * switches the scene to the home view when the home button is pressed
     */
    @FXML
    void mouseHome() {
        switchView("HomeTimeline");
    }
     /**
     * switches the scene to the favourites view when the favourites button is pressed
     */
    @FXML
    void mouseFavourited() {switchView("Favourites");}

    /**
     * changes the main scene's center to the asked scene
     *
     * @param scene the scene to be shown
     */
    private void switchView(String scene) {
        selectBtn(scene);
        var settingsRoot = settingsScene.getRoot();
        switch (scene) {
            case "PostedToots" -> {
                showAccountToots();
                currentView = "PostedToots";
            }
            case "HomeTimeline" -> {
                showHomeTimeline();
                currentView = "HomeTimeline";
            }
            case "Following" -> {
                showFollowing();
                currentView = "Following";
            }
            case "Followers" -> {
                showFollowers();
                currentView = "Followers";
            }
            case "Favourites" -> {
                showFavourites();
                currentView = "Favourites";
            }
            case "Settings" -> {
                mainPane.setCenter(settingsRoot);
                //BUG: If I don't update the pane the settings window doesn't show up
                mainPane.setCenter(accountListView);
                mainPane.setCenter(settingsRoot);
                currentView = "Settings";
            }

        }
    }


    /**
     * This method sets the style of the button that has just been pressed
     * so it can differentiate it from the rest
     * @param btn is the selected button
     */
    public void selectBtn(String btn){
        if(btn.equals("PostedToots") && currentAccount.getId().equals(userAccount.getId())) profileBtn.setId("selected-button");
        else profileBtn.setId("");
        if(btn.equals("HomeTimeline")) homeBtn.setId("selected-button");
        else homeBtn.setId("");
        if(btn.equals("Following")) followingBtn.setId("selected-button");
        else followingBtn.setId("");
        if(btn.equals("Followers")) followersBtn.setId("selected-button");
        else followersBtn.setId("");
        if(btn.equals("Settings")) settingsBtn.setId("selected-button");
        else settingsBtn.setId("");
        if(btn.equals("Favourites")) favsBtn.setId("selected-button");
        else favsBtn.setId("");

    }

    /**
     * Updates the statuses of the current account
     */
    public void updateAccountToots() {
        try {
            accountToots = BusinessLogic.getStatuses(currentAccount);

            var it = accountToots.listIterator();
            while (it.hasNext()) {
                var status = it.next();
                if (status.getReblog() != null) {
                    it.set(status.getReblog());
                }

            }
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * gets the statuses of the user and shows them in a list
     */
    public void showAccountToots() {
        tootListView.getItems().setAll(accountToots);
        tootListView.scrollTo(0);
        mainPane.setCenter(tootListView);
    }
    /**
     * Updates the home view of the user
     */
    public void updateHomeTimeline() {
        homeTimeline = CompletableFuture.supplyAsync(() -> {
            try {
                return BusinessLogic.getHomeTimeline().getPart();
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        });

    }
    /**
     * Shows the timeline of the user
     */
    public void showHomeTimeline() {
        try {
            tootListView.getItems().setAll(homeTimeline.get());
            tootListView.scrollTo(0);
            mainPane.setCenter(tootListView);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the list of toots that the user has favourited.
     */
    public void updateFavourites() {
        try {
            favouritedToots = BusinessLogic.getFavouritedStatuses();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Shows the list of toots that the user has favourited.
     */
    private void showFavourites() {
        if(favouritedToots!=null) {
            tootListView.getItems().setAll(favouritedToots);
            tootListView.scrollTo(0);
            mainPane.setCenter(tootListView);
        }
    }

    /**
     * Gets the list of accounts that the user is following and shows it
     */
    public void updateFollowingList() {
        followingList = CompletableFuture.supplyAsync(() -> {
            try {
                return BusinessLogic.getFollowing(currentAccount);
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        });

    }
    /**
     * Shows the following list of the account that is currently logged in
     */
    public void showFollowing() {
        try {
            accountListView.getItems().setAll(followingList.get());
            accountListView.scrollTo(0);
            mainPane.setCenter(accountListView);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets the list of accounts that are following the user and shows it
     */
    public void updateFollowersList() {
        followersList = CompletableFuture.supplyAsync(() -> {
            try {
                return BusinessLogic.getFollowers(currentAccount);
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * Shows the followers list of the account that is currently logged in
     */
    public void showFollowers() {
        try {
            accountListView.getItems().setAll(followersList.get());
            accountListView.scrollTo(0);
            mainPane.setCenter(accountListView);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the profile picture, username and display name of the user
     */
    public void updateBanner() {
        profilePic.setImage(ImageCache.get(currentAccount.getAvatar()));
        userNameLabel.setText("@" + currentAccount.getUsername());
        displayNameLabel.setText(currentAccount.getDisplayName());
        showFollowButton();
    }

    /**
     * Shows the follow button if the user is not in their own profile
     */
    public void showFollowButton() {
        if (currentAccount.getId().equals(userAccount.getId()))
            followBtn.setVisible(false);
        else {
            if (RelationshipCache.get(currentAccount).isFollowing())
                followBtn.setText(showUnfollow());
            else
                followBtn.setText(showFollow());

            followBtn.setVisible(true);
        }
    }

    /**
     * Shows the Follow button in the language selected
     * @return l, the language selected in the settings
     */
    public String showFollow(){
        String l = "sdkjkfhis";
        if (SettingsController.len.equals("eus_ES")){
            l = "Jarraitu";
        } else if (SettingsController.len.equals("es_ES")){
            l = "Seguir";
        } else if (SettingsController.len.equals("eng_US")){
            l = "Follow";
        }
        return l;
    }
    /**
     * Shows the Unfollow button in the language selected
     * @return l, the language selected in the settings
     */
    public String showUnfollow(){
        String l = "sdkjkfhis";
        if (SettingsController.len.equals("eus_ES")){
            l = "Jarraitzen";
        } else if (SettingsController.len.equals("es_ES")){
            l = "Siguiendo";
        } else if (SettingsController.len.equals("eng_US")){
            l = "Unfollow";
        }
        return l;
    }

    /**
     * Posts the toot written in the text area
     */
    @FXML
    public void postToot() {
        String toot = newTootArea.getText();
        try {
            BusinessLogic.postStatus(toot);
            newTootArea.clear();
            showAccountToots();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the status in the given list.
     * @param id the id of the status to update
     */
    private void updateStatus(List<Status> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                try {
                    list.set(i, BusinessLogic.getStatus(id));
                    Platform.runLater(() -> tootListView.getItems().setAll(list));
                } catch (BigBoneRequestException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    /**
     * Updates the given status in the list that is currently being shown.
     * @param id the id of the status to update
     */
    public void updateStatus(String id) {
        switch (currentView) {
            case "PostedToots" -> {updateStatus(accountToots, id);}
            case "Favourites" -> {updateStatus(favouritedToots, id);}
            case "HomeTimeline" -> {
                try {
                    updateStatus(homeTimeline.get(), id);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * Shows the settings view
     */
    public void loadSettingsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"), t(SettingsController.len));
            settingsScene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the profile to the given id
     *
     * @param account the account to update
     */
    public void setProfile(Account account) {
        if (account != currentAccount) {
            currentAccount = account;
            updateFollowingList();
            updateFollowersList();
            updateBanner();
            updateAccountToots();
            updateFavourites();
            updateRelationshipCache();
        }
        switchView("PostedToots");
    }
    /**
     * Updates the relationship cache
     */
    public void updateRelationshipCache() {
        CompletableFuture.runAsync(() -> {
            try {
                homeTimeline.get();
                followingList.get();
                followersList.get();
                RelationshipCache.processPending();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * Shows if the current user is following their followers and following list
     */
    @FXML
    void followCurrentAccount() {
        try {
            if (!RelationshipCache.get(currentAccount).isFollowing()) {
                BusinessLogic.followAccount(currentAccount.getId());
                followBtn.setText(showUnfollow());
            } else {
                BusinessLogic.unfollowAccount(currentAccount.getId());
                followBtn.setText(showFollow());
            }
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    public void followAccount(Account acc) {
        try {
            BusinessLogic.followAccount(acc);
            if (currentAccount == userAccount)
                updateFollowingList();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    public void unfollowAccount(Account acc) {
        try {
            BusinessLogic.unfollowAccount(acc);
            if (currentAccount == userAccount)
                updateFollowingList();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }


}
