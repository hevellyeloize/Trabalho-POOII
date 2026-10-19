module com.biblioteca.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.biblioteca.biblioteca to javafx.fxml;
    opens com.biblioteca.biblioteca.model to javafx.base;

    exports com.biblioteca.biblioteca;
    exports com.biblioteca.biblioteca.model;
    exports com.biblioteca.biblioteca.dao;
    exports com.biblioteca.biblioteca.conexao;
    exports com.biblioteca.biblioteca.controller;
    opens com.biblioteca.biblioteca.controller to javafx.fxml;
}