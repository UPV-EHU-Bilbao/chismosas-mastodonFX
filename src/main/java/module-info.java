module eus.ehu.chismosas.mastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires bigbone;
    requires org.jsoup;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires okhttp3;


    exports eus.ehu.chismosas.mastodonfx.presentation;
    opens eus.ehu.chismosas.mastodonfx.presentation to javafx.fxml;
}