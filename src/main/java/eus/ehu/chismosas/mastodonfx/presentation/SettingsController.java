package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

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
        MainApplication.setTheme("dark");
    }

    @FXML
    public void selectLightTheme(ActionEvent event) {
        darkBtn.setSelected(false);
        MainApplication.setTheme("light");
    }

    @FXML
    public void changeUsername(ActionEvent event) {
        String user = usernameField.getText();
        try {
            BusinessLogic.changeDisplayName(user);
            MainController.getInstance().updateBanner();
        } catch (Exception e) {
            usernameField.setText("Error");
        }
    }

    @FXML
    public void logOut() {
        MainApplication.logout();
    }
}
