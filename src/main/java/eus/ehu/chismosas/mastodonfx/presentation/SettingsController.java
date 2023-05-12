package eus.ehu.chismosas.mastodonfx.presentation;

import eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import social.bigbone.api.entity.Account;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static eus.ehu.chismosas.mastodonfx.businesslogic.BusinessLogic.getLoggedAccount;
import static javafx.application.Application.launch;


import java.io.File;

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
    private ComboBox<String> lenguage;


    static String len = Locale.getDefault().toString();


    @FXML
    public void initialize(){
        setUpComboBox();
    }

    private void setUpComboBox(){
        String[] lenguages = {"ES","EUS","ENG"};
        lenguage.getItems().addAll(lenguages);
    }
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
            if (user.length()<1) throw new Exception();
            BusinessLogic.changeDisplayName(user);
            MainController.getInstance().updateBanner();
        } catch (Exception e) {
            usernameField.setText("");
            usernameField.setPromptText("Error");
        }
    }

    /**
     * This method changes the lenguage of the application
     *
     * @param event
     */
    @FXML
   public void chooseLenguage(ActionEvent event) {
       // Account account = BusinessLogic.getUserAccount();
       if (lenguage.getValue() == "EUS"){
           len = "eus_ES";
           MainApplication.t(len);
           MainApplication.setProfile();
       } else if (lenguage.getValue() == "ENG"){
           len = "eng_US";
           MainApplication.t(len);
           MainApplication.setProfile();
       } else if (lenguage.getValue() == "ES"){
           len = "es_ES";
           MainApplication.t(len);
           MainApplication.setProfile();
       }
    }

    @FXML
    public void logOut() {
        MainApplication.logout();
    }
}
