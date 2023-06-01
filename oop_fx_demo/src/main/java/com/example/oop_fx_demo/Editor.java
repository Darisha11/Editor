package com.example.oop_fx_demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Editor extends Application {
    @Override
    public void start(Stage mystage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Editor.class.getResource("editor.fxml"));
        loader.setControllerFactory(t -> new EditorController(new EditorModel()));

        mystage.getIcons().add(new Image("https://cdn1.iconfinder.com/data/icons/browser-5/100/ui-brower-go-09-1024.png"));
        mystage.setTitle("Текстовый редактор");
        mystage.setScene(new Scene(loader.load()));
        mystage.show();

        ModalWindow.newWindow("Текстовый редактор");

    }

    public static void main(String[] args) {
        launch(args);
    }
}