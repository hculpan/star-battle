package org.culpan.starbattle.sprites;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.Date;

public class FuelBonus extends MovingObject {
    long created;

    int millisOfLife;

    int value;

    Image item;

    public FuelBonus(int millisOfLife, int value, double x, double y) {
        this.created = new Date().getTime();
        this.millisOfLife = millisOfLife;
        this.value = value;
        this.x = x;
        this.y = y;
        this.speed = 0.0;
        this.direction = 0;

        item = new Image("/fuel-bonus.png");
        ImageView imageView = new ImageView(item);
        imageView.setScaleX(0.065);
        imageView.setScaleY(0.065);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        item = imageView.snapshot(parameters, null);
    }

    @Override
    public void update() {
        long current = new Date().getTime();
        destroyed = current > created + millisOfLife;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(item, x, y);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
