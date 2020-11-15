package org.culpan.starbattle.sprites;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;

public abstract class MovingObject {
    static Random random = new Random();

    double x;

    double y;

    double width;

    double height;

    double direction;

    double lastDirection;

    double speed;

    double worldWidth;

    double worldHeight;

    boolean destroyed = false;

    public abstract void update();

    public abstract void draw(GraphicsContext gc);

    public boolean removeFromGame() {
        return destroyed;
    }



    @SuppressWarnings("Duplicates")
    protected void updateLocation() {
        if(speed > 0.1) {
            double radians = Math.toRadians(direction);
            double xDelta = Math.sin(radians);
            double yDelta = Math.cos(radians);
            x += speed * xDelta;
            y += speed * (yDelta * -1);
        }

        if (x < 0) {
            x = worldWidth + x;
        } else if (x > worldWidth) {
            x = x - worldWidth;
        }

        if (y < 0) {
            y = worldHeight + y;
        } else if (y > worldHeight) {
            y = y - worldHeight;
        }
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(double lastDirection) {
        this.lastDirection = lastDirection;
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public void setWorldWidth(double worldWidth) {
        this.worldWidth = worldWidth;
    }

    public double getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(double worldHeight) {
        this.worldHeight = worldHeight;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }
}

