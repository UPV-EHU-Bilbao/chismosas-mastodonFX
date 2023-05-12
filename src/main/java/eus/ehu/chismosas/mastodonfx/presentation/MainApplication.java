package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import social.bigbone.api.entity.Account;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the main class of the application. It loads the main view and shows it on the screen.
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainApplication extends Application {
    private static MainApplication instance;
    public static Stage mainStage;

    private static final String lightTheme = MainApplication.class.getResource("styles/light.css").toExternalForm();
    private static final String darkTheme = MainApplication.class.getResource("styles/dark.css").toExternalForm();
    private static String currentTheme;

    /**
     * Logs In the user and show the main view
     * @param account, the account of the user that wants to LogIn
     */
    public static void login(Account account) {
        BusinessLogic.login(account);
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"), t(SettingsController.len));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(currentTheme);
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - @" + account.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Logs out the current user and shows the account selection view
     */
    public static void logout() {
        BusinessLogic.logout();
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"), t(SettingsController.len));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(currentTheme);
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - Account selection");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the instance of the main application
     * @param leng, the language of the application
     * @return a local specific object
     */
   public static ResourceBundle t(String leng){
        return ResourceBundle.getBundle("strings", new Locale(leng));
    }

    /**
     * Changes the theme of the application
     *
     * @param theme the theme to change to
     */
    public static void setTheme(String theme) {
        switch (theme) {
            case "light" -> currentTheme = lightTheme;
            case "dark" -> currentTheme = darkTheme;
            default -> throw new IllegalArgumentException("Invalid theme");
        }
        instance.mainStage.getScene().getStylesheets().setAll(currentTheme);
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Shows the main view on the screen
     */
    public static void setProfile() {
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"), t(SettingsController.len));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            instance.mainStage.setScene(scene);
            instance.mainStage.show();
            instance.mainStage.requestFocus();
            instance.mainStage.getScene().getStylesheets().setAll(currentTheme);
           // instance.mainStage.setTitle("MastodonFX - Profile");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows the main view on the screen
     * @param stage, the stage of the application
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.instance = this;
        this.mainStage = stage;
        stage.getIcons().addAll(new Image(MainApplication.class.getResourceAsStream("logo/logo_16px.png")),
                new Image(MainApplication.class.getResourceAsStream("logo/logo_32px.png")));
        currentTheme = darkTheme;

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("account-selection.fxml"), t(SettingsController.len));
        ResourceBundle bundle = t(SettingsController.len);

        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setTitle(bundle.getString("MastodonFX - Account selection"));
        scene.getStylesheets().add(currentTheme);

        mainStage.setTitle("MastodonFX - Account selection");
        mainStage.setScene(scene);
        mainStage.show();
        mainStage.requestFocus();


    }
}