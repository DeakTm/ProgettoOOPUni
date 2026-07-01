package com.project.uninataskboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Uninataskboard extends Application {

    @Override
    public void start(Stage stage) {

        Button btn = new Button("JavaFX funziona!");

        StackPane root = new StackPane(btn);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Progetto Uni");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}