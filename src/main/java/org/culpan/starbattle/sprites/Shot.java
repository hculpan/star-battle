package org.culpan.starbattle.sprites;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Shot extends MovingObject {
    Image shot;

    public Shot(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = 10;

        loadShotImage();
    }

    private void loadShotImage() {
        shot = new Image("/shot.png");
        ImageView imageView = new ImageView(shot);
        imageView.setScaleX(0.065);
        imageView.setScaleY(0.065);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        shot = imageView.snapshot(parameters, null);
        this.width = shot.getWidth();
        this.height = shot.getHeight();
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void updateLocation() {
        if(speed > 0.1) {
            double radians = Math.toRadians(direction);
            double xDelta = Math.sin(radians);
            double yDelta = Math.cos(radians);
            x += speed * xDelta;
            y += speed * (yDelta * -1);

        }
    }

    @Override
    public void update() {
        updateLocation();
        destroyed = x <= 0 || x >= worldWidth || y <= 0 || y >= worldHeight || destroyed;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(shot, x, y);
    }
}
