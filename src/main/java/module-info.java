module ehu.eus.chismosas.mastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires okhttp3;


    exports ehu.eus.chismosas.mastodonfx.presentation;
    opens ehu.eus.chismosas.mastodonfx.presentation to javafx.fxml;
}