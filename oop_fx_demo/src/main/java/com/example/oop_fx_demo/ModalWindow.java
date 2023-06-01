package com.example.oop_fx_demo;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class ModalWindow {
    public static void newWindow(String title){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        VBox content = new VBox();
        Button btn = new Button("начать работу");
        btn.setOnAction(event->window.close());
        final Text text = new Text("   Добро пожаловать\nв текстовый редактор!");

        content.getChildren().addAll(text);
        content.getChildren().addAll(btn);
        content.setAlignment(Pos.CENTER);

        btn.setStyle("-fx-font-size: 2em; ");
        text.setFont(Font.font("Lucida Sans Unicode", FontWeight.BOLD, 36));
        text.setFill(Color.WHITE);

        Scene scene = new Scene(content, 800,600);
        content.setBackground(new Background(
                        createImage("https://i.pinimg.com/originals/e3/da/1d/e3da1dd469652fc34cd6ae9759ba81d4.jpg")
                )
        );
        window.getIcons().add(new Image("https://cdn1.iconfinder.com/data/icons/browser-5/100/ui-brower-go-09-1024.png"));
        window.setScene(scene);
        window.setTitle(title);
        window.showAndWait();
    }

    private static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }

}
