package org.culpan.starbattle.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class MainController {
    Stage stage;

    @FXML
    public Pane mainPane;

    @FXML
    public Canvas mainCanvas;

    @FXML
    Canvas backgroundCanvas;

    @FXML
    public Canvas statusCanvas;

    public void handleResize() {
        mainCanvas.setWidth(stage.getWidth());
        mainCanvas.setHeight(stage.getHeight());
        backgroundCanvas.setWidth(stage.getWidth());
        backgroundCanvas.setHeight(stage.getHeight());
        statusCanvas.setWidth(stage.getWidth());
        statusCanvas.setHeight(stage.getHeight());
        mainPane.setPrefWidth(stage.getWidth());
        mainPane.setPrefHeight(stage.getHeight());

        // Draw stars
        Image stars = drawStars((int)backgroundCanvas.getWidth(), (int)backgroundCanvas.getHeight());
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();
        gc.drawImage(stars, 0, 0);
    }

    public Image drawStars(int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw  = img.getPixelWriter();
        Random rnd = new Random();
        for (int y = 0 ; y < height ; y++) {
            for (int x = 0 ; x < width ; x++) {
                if (rnd.nextInt(1000) == 1) {
                    pw.setColor(x, y, Color.WHITE);
                } else {
                    pw.setColor(x, y, Color.BLACK);
                }
            }
        }
        return img;
    }
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
