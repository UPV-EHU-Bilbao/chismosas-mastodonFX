module eus.ehu.chismosas.mastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires bigbone;


    exports eus.ehu.chismosas.mastodonfx.presentation;
    opens eus.ehu.chismosas.mastodonfx.presentation to javafx.fxml;
}