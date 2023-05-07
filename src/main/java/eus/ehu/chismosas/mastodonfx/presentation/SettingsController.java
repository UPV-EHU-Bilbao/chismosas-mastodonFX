package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class SettingsController {
    @FXML
    private RadioButton darkBtn;

    @FXML
    private RadioButton lightBtn;

    @FXML
    private ImageView homeImg;

    @FXML
    private Button usernameBtn;

    @FXML
    private TextField usernameField;


    @FXML
    public void selectDarkTheme(ActionEvent event) {
        lightBtn.setSelected(false);
        MainController.getInstance().darkButton();
        AccountSelection.getInstance().setDarkTheme();
    }

    @FXML
    public void selectLightTheme(ActionEvent event) throws IOException {
        darkBtn.setSelected(false);
        MainController.getInstance().lightButton();
        AccountSelection.getInstance().setLightTheme();
    }

    @FXML
    public void changeUsername(ActionEvent event) {
        String user = usernameField.getText();
        try{
            BusinessLogic.changeUsername(user);
            MainController.getInstance().updateBanner();
        }
        catch (Exception e){
            usernameField.setText("Error");
        }
    }

    @FXML
    public void logOut() {
        MainApplication.logout();
    }
}
