package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    private Stage mainStage;


    public static void login(Account account) {
        BusinessLogic.login(account);
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"), t());
        try {
            Scene scene = new Scene(fxmlLoader.load());
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - @" + account.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logout() {
        BusinessLogic.logout();
        var fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"), t());
        try {
            Scene scene = new Scene(fxmlLoader.load());
            instance.mainStage.setScene(scene);
            instance.mainStage.setTitle("MastodonFX - Account selection");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceBundle t(){
        return ResourceBundle.getBundle("strings", new Locale("eus_ES"));
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.instance = this;
        this.mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("account-selection.fxml"),
                t());
        ResourceBundle bundle = t();

        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setTitle(bundle.getString("MastodonFX - Account selection"));
        mainStage.setScene(scene);
        mainStage.show();
        mainStage.requestFocus();


    }
}