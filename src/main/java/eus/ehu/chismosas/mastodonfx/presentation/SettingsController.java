package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML
    private RadioButton darkBtn;

    @FXML
    private RadioButton lightBtn;

    @FXML
    private TextField passwordField;

    @FXML
    private Button usernameBtn;

    @FXML
    private Button usernameBtn1;

    @FXML
    private TextField usernameField;

    @FXML
    public void selectDarkTheme(ActionEvent event) {
        lightBtn.setSelected(false);
        //MainController.getInstance().getScene().getStylesheets().add(getClass().getResource("darkmode.css").toExternalForm());

    }

    @FXML
    public void selectLightTheme(ActionEvent event) {
        darkBtn.setSelected(false);
        //MainController.getInstance().getScene().getStylesheets().clear();
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
