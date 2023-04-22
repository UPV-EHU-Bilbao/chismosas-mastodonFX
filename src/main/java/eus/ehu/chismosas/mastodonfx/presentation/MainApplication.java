package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the main class of the application. It loads the main view and shows it on the screen.
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class MainApplication extends Application {

    private static MainApplication instance;
    private Stage mainStage;
    public static void setScene(Scene scene) {instance.mainStage.setScene(scene);}
    public static void setTitle(String title) {instance.mainStage.setTitle(title);}

    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.instance = this;
        this.mainStage = stage;

        BusinessLogic.loadAccountLogins();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account-selection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setTitle("MastodonFX - Account selection");
        mainStage.setScene(scene);
        mainStage.show();
        mainStage.requestFocus();
    }

    @Override
    public void stop() throws Exception{
        BusinessLogic.saveAccountLogins();
    }

    public static void main(String[] args) {
        launch();
    }
}