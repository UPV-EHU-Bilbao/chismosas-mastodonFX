module ehu.eus.chismosas.mastodonfx {
    requires javafx.controls;
    requires javafx.fxml;


    exports ehu.eus.chismosas.mastodonfx.presentation;
    opens ehu.eus.chismosas.mastodonfx.presentation to javafx.fxml;
}