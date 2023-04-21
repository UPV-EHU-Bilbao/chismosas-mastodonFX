package eus.ehu.chismosas.mastodonfx.presentation;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class AccountSelection {

    @FXML
    private ListView<?> accountsList;

    @FXML
    void chooseAccount(MouseEvent event){
        System.out.println("Account selected");

    }

}
