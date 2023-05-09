package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import eus.ehu.chismosas.mastodonfx.businesslogic.RelationshipCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import social.bigbone.api.Pageable;
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
    private Future<List<Account>> followersList;
    private Future<List<Account>> followingList;
    private Future<Pageable<Status>> homeTimeline;


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


        tootListView.setStyle("-fx-control-inner-background: #18181b");
        accountListView.setStyle("-fx-control-inner-background: #18181b");

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

    @FXML
    void mouseSettings() {
        switchView("Settings");
    }

    @FXML
    void mouseHome() {
        switchView("HomeTimeline");
    }

    /**
     * changes the main scene's center to the asked scene
     *
     * @param scene the scene to be shown
     */
    private void switchView(String scene) {
        var settingsRoot = settingsScene.getRoot();
        switch (scene) {
            case "PostedToots" -> {
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);
                followingBtn.setStyle("-fx-background-color:  #18181b");
                followersBtn.setStyle("-fx-background-color:  #18181b");

                showAccountToots();
            }
            case "HomeTimeline" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);
                settingsBtn.setEffect(null);

                showHomeTimeline();
            }
            case "Following" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(dropShadow);
                followersBtn.setEffect(null);
                settingsBtn.setEffect(null);
                followersBtn.setStyle("-fx-background-color:  #18181b");
                profileBtn.setStyle("-fx-background-color: #18181b");
                followingBtn.setStyle("-fx-background-color: #212124");

                showFollowing();
            }
            case "Followers" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(dropShadow);
                settingsBtn.setEffect(null);
                profileBtn.setStyle("-fx-background-color: #18181b");
                followingBtn.setStyle("-fx-background-color: #18181b");
                followersBtn.setStyle("-fx-background-color: #212124");

                showFollowers();
            }
            case "Settings" -> {
                profileBtn.setEffect(null);
                followingBtn.setEffect(null);
                followersBtn.setEffect(null);
                settingsBtn.setEffect(dropShadow);
                mainPane.setCenter(settingsRoot);

                //BUG: If I don't update the pane the settings window doesn't show up
                mainPane.setCenter(accountListView);
                mainPane.setCenter(settingsRoot);
            }

        }
    }

    public void updateAccountToots() {
        try {
            accountToots = BusinessLogic.getStatuses(currentAccount);
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

    public void updateHomeTimeline() {
        homeTimeline = CompletableFuture.supplyAsync(() -> {
            try {
                return BusinessLogic.getHomeTimeline();
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void showHomeTimeline() {
        try {
            tootListView.getItems().setAll(homeTimeline.get().getPart());
            tootListView.scrollTo(0);
            mainPane.setCenter(tootListView);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
                followBtn.setText("Unfollow");
            else
                followBtn.setText("Follow");

            followBtn.setVisible(true);
        }
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
     * Requests the status with the given id and updates it in the list
     *
     * @param id the id of the status to update
     */
    public void updateStatus(String id) {
        var iterator = tootListView.getItems().listIterator();
        while (iterator.hasNext()) {
            var status = iterator.next();
            if (status.getId().equals(id)) {
                try {
                    var updatedStatus = BusinessLogic.getStatus(id);
                    iterator.set(updatedStatus);
                } catch (BigBoneRequestException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void loadSettingsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"), t());
            settingsScene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void lightButton() {
        mainPane.setStyle("-fx-background-color: #ffffff");
        userNameLabel.setStyle("-fx-text-fill: #000000");
        displayNameLabel.setStyle("-fx-text-fill: #000000");
        followersBtn.opacityProperty().setValue(0.4);
        followingBtn.opacityProperty().setValue(0.4);
        profileBtn.opacityProperty().setValue(0.4);
        settingsBtn.opacityProperty().setValue(0.4);
        tootListView.setStyle("-fx-background-color: #ffffff");
        accountListView.setStyle("-fx-background-color: #ffffff");
    }

    public void darkButton() {
        mainPane.setStyle("-fx-background-color: #18181b");
        userNameLabel.setStyle("-fx-text-fill: #ffffff");
        displayNameLabel.setStyle("-fx-text-fill: #ffffff");
        followersBtn.opacityProperty().setValue(1);
        followingBtn.opacityProperty().setValue(1);
        profileBtn.opacityProperty().setValue(1);
        settingsBtn.opacityProperty().setValue(1);
        tootListView.setStyle("-fx-background-color: #18181b");
        accountListView.setStyle("-fx-background-color: #18181b");
    }

    /**
     * Changes the profile to the given id
     *
     * @param account the account to update
     */
    public void setProfile(Account account) {

        if (account.getId().equals(userAccount.getId())) {
            profileBtn.setEffect(dropShadow);
            profileBtn.setStyle("-fx-background-color: #212124");
        } else {
            profileBtn.setEffect(null);
            profileBtn.setStyle("-fx-background-color: #18181b");
        }

        if (account != currentAccount) {
            currentAccount = account;
            updateFollowingList();
            updateFollowersList();
            updateBanner();
            updateAccountToots();
            updateRelationshipCache();
        }

        switchView("PostedToots");

    }

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

    @FXML
    void followAccount() {
        try {
            if (!RelationshipCache.get(currentAccount).isFollowing()) {
                BusinessLogic.followAccount(currentAccount.getId());
                followBtn.setText("Unfollow");
            } else {
                BusinessLogic.unfollowAccount(currentAccount.getId());
                followBtn.setText("Follow");
            }
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

}
