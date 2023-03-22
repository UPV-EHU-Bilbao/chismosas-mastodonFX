package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MastodonFX");
        stage.setScene(scene);
        stage.show();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}