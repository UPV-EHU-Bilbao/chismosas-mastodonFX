package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SettingsController {
    @FXML
    private RadioButton darkBtn;

    @FXML
    private RadioButton lightBtn;

    @FXML
    private Button usernameBtn;

    @FXML
    private TextField usernameField;

    @FXML
    public void selectDarkTheme(ActionEvent event) {
        lightBtn.setSelected(false);
        MainController.getInstance().darkButton();
        //MainController.getInstance().getScene().getStylesheets().add(getClass().getResource("dark.css").toExternalForm());

    }

    @FXML
    public void selectLightTheme(ActionEvent event) throws IOException {
        darkBtn.setSelected(false);
        MainController.getInstance().lightButton();
        //MainController.getInstance().getScene().getStylesheets().add(getClass().getResource("light.css").toExternalForm());
    }

    @FXML
    public void changeUsername(ActionEvent event) {
        String user = usernameField.getText();
        try{
            BusinessLogic.changeUsername(user);
            MainController.getInstance().setProfile();
        }
        catch (Exception e){
            usernameField.setText("Error");
        }
    }
}
