module eus.ehu.chismosas.mastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires bigbone;
    requires org.jsoup;
    requires com.google.gson;
    requires java.sql;


    exports eus.ehu.chismosas.mastodonfx.presentation;
    opens eus.ehu.chismosas.mastodonfx.presentation to javafx.fxml;
}