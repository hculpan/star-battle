package org.culpan.starbattle.sprites;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Asteroid extends MovingObject {
    public enum SIZE { small, medium, large}

    double rotateChange;

    double currentRotation;

    double lastRotation;

    Image asteroid;

    Image rotatedAsteroid;

    public Asteroid(double x, double y, int direction, double speed, double rotateChange, SIZE size) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.rotateChange = rotateChange;
        this.currentRotation = direction;

        loadAsteroidImage(size);
    }

    protected void loadAsteroidImage(SIZE size) {
        asteroid = new Image(String.format("/asteroid-%s.png", size.toString()));
        ImageView imageView = new ImageView(asteroid);
        imageView.setScaleX(0.065);
        imageView.setScaleY(0.065);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        asteroid = imageView.snapshot(parameters, null);
        this.width = asteroid.getWidth();
        this.height = asteroid.getHeight();
    }

    @Override
    public void update() {
        updateLocation();
    }

    @Override
    public void draw(GraphicsContext gc) {
        currentRotation += rotateChange;
        if ((rotateChange > 0 && currentRotation - 5 > lastRotation) ||
                (currentRotation + 5 < lastRotation)) {
            ImageView iv = new ImageView(asteroid);
            iv.setRotate(currentRotation);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedAsteroid = iv.snapshot(params, null);
        }

        gc.drawImage(rotatedAsteroid, x, y);
    }
}
