package org.culpan.starbattle.sprites;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.culpan.starbattle.controllers.MainController;

import java.util.Date;

public class Starship extends MovingObject {
    final double MAX_SPEED = 6;

    final double DIR_CHANGE = 4;

    final int MAX_ENGINE_SHIFT = 10;

    final int ROCKET_FRAMES = 4;

    boolean rightPressed = false;

    boolean leftPressed = false;

    boolean accelerate = false;

    public long fuel = 1000;

    public long score = 0;

    boolean hit = false;

    long wasHitTime;

    int framesSinceLastEngineChange = 0;

    int lastEngineFrame = 1;

    double lastDirection = -1;

    MainController mainController;

    ImageView[] starshipRocket;

    ImageView starship;

    Image rotatedStarship;

    public Starship(MainController mainController, double startingX, double startingY) {
        this.mainController = mainController;
        this.x = startingX;
        this.y = startingY;
        this.direction = 0; random.nextInt(360);
        if (direction == 0) {
            lastDirection = 1;
        } else {
            lastDirection = 0;
        }
        this.speed = 0.0;

        loadShipFrames();
    }

    protected void loadShipFrames() {
        starshipRocket = new ImageView[ROCKET_FRAMES];
        for (int i = 0; i < ROCKET_FRAMES; i++) {
            starshipRocket[i] = new ImageView(String.format("/starship-rocket-%d.png", i + 1));
            starshipRocket[i].setFitWidth(50);
            starshipRocket[i].setPreserveRatio(true);
            starshipRocket[i].setSmooth(true);
            starshipRocket[i].setCache(true);
        }

        starship = new ImageView("/starship-no-rocket.png");
        starship.setFitWidth(50);
        starship.setPreserveRatio(true);
        starship.setSmooth(true);
        starship.setCache(true);
        this.width = 50;
        this.height = 50;
    }

    @Override
    public boolean removeFromGame() {
        return false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        boolean setRotate = Math.abs(lastDirection - direction) > 1;
        if (isAccelerate() && framesSinceLastEngineChange > MAX_ENGINE_SHIFT) {
            lastEngineFrame++;
            if (lastEngineFrame == ROCKET_FRAMES) {
                lastEngineFrame = 0;
            }
            framesSinceLastEngineChange = 0;
            setRotate = true;
        } else if (isAccelerate()) {
            framesSinceLastEngineChange++;
        } else {
            lastEngineFrame = 0;
            framesSinceLastEngineChange = 0;
            setRotate = true;
        }

        if (setRotate) {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            if (isAccelerate()) {
                starshipRocket[lastEngineFrame].setRotate(direction);
                rotatedStarship = starshipRocket[lastEngineFrame].snapshot(params, null);
            } else {
                starship.setRotate(direction);
                rotatedStarship = starship.snapshot(params, null);
            }
        }

        double xOffset = (rotatedStarship.getWidth() - 50) / 2;
        double yOffset = (rotatedStarship.getHeight() - 50) / 2;
        gc.drawImage(rotatedStarship, x - xOffset, y - yOffset);
    }

    public void wasHit() {
        setFuel(fuel - 100);
        setScore(score - 50);
        hit = true;
        wasHitTime = new Date().getTime();
    }

    @Override
    public void update() {
        if (hit) {
            long current = new Date().getTime();
            if (current > wasHitTime + 1000) {
                hit = false;
            } else {
                accelerate = false;
                speed = 0;
            }
        }

        if (accelerate) {
            setFuel(fuel - 1);

            if (speed < 1.0) {
                speed = 1;
            } else if (speed > MAX_SPEED) {
                speed = MAX_SPEED;
            } else {
                speed = speed * 1.05;
            }
        } else {
            if (speed < .15) {
                speed = 0;
            } else {
                speed = speed * 0.90;
            }
        }

        setDirection();
    }

    private void setDirection() {
        updateLocation();

        if (leftPressed && !hit) {
            direction -= DIR_CHANGE;
            if (direction < 0) {
                direction = 360 + direction;
            }
        } else if (rightPressed && !hit) {
            direction += DIR_CHANGE;
            if (direction > 360) {
                direction = 0 + (direction - 360);
            }
        }
    }

    public boolean isAccelerate() {
        return accelerate;
    }

    public void setAccelerate(boolean accelerate) {
        if (accelerate && fuel > 0) {
            this.accelerate = true;
        } else {
            this.accelerate = false;
        }
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public long getFuel() {
        return fuel;
    }

    public void setFuel(long fuel) {
        if (fuel < 0) {
            this.fuel = 0;
        } else {
            this.fuel = fuel;
        }
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
