package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import social.bigbone.api.entity.Account;

import java.io.IOException;

/**
 * This is the main class of the application. It loads the main view and shows it on the screen.
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainApplication extends Application {
    private static MainApplication instance;
    private Stage mainStage;

    private static final String lightTheme = MainApplication.class.getResource("styles/light.css").toExternalForm();
    private static final String darkTheme = MainApplication.class.getResource("styles/dark.css").toExternalForm();
    private static String currentTheme;


    public static void login(Account account) {
        BusinessLogic.login(account);
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(currentTheme);
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - @" + account.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logout() {
        BusinessLogic.logout();
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(currentTheme);
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - Account selection");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTheme(String theme) {
        switch (theme) {
            case "light" -> currentTheme = lightTheme;
            case "dark" -> currentTheme = darkTheme;
            default -> throw new IllegalArgumentException("Invalid theme");
        }
        instance.mainStage.getScene().getStylesheets().setAll(currentTheme);
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.instance = this;
        this.mainStage = stage;
        currentTheme = darkTheme;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(currentTheme);

        mainStage.setTitle("MastodonFX - Account selection");
        mainStage.setScene(scene);
        mainStage.show();
        mainStage.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}