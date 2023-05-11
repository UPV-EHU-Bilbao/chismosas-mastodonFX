package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.File;

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
    private Button avatarButton;

    @FXML
    public void selectDarkTheme(ActionEvent event) {
        lightBtn.setSelected(false);
        MainController.getInstance().darkButton();
        //MainController.getInstance().getScene().getStylesheets().add(getClass().getResource("dark.css").toExternalForm());

    }

    @FXML
    public void selectLightTheme(ActionEvent event) {
        darkBtn.setSelected(false);
        MainController.getInstance().lightButton();
        //MainController.getInstance().getScene().getStylesheets().add(getClass().getResource("light.css").toExternalForm());
    }

    @FXML
    public void changeUsername(ActionEvent event) {
        String user = usernameField.getText();
        try {
            BusinessLogic.changeDisplayName(user);
            MainController.getInstance().updateBanner();
        } catch (Exception e) {
            usernameField.setText("");
            usernameField.setPromptText("Error");
        }
    }

    @FXML
    public void selectAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                BusinessLogic.changeAvatar(selectedFile);
            } catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void logOut() {
        MainApplication.logout();
    }
}
