package org.culpan.starbattle;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.culpan.starbattle.controllers.MainController;
import org.culpan.starbattle.sprites.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Animator extends AnimationTimer {
    final static Random random = new Random();

    private final long[] frameTimes = new long[100];

    private int frameTimeIndex = 0 ;

    private boolean arrayFilled = false ;

    MainController mainController;

    Canvas canvas;

    boolean paused = false;

    Starship starship;

    boolean showFrameRate = false;

    List<MovingObject> movingObjects = new ArrayList<>();

    public Animator(MainController mainController) {
        this.mainController = mainController;
        this.canvas = mainController.mainCanvas;
    }

    public void setupGame() {
        starship = new Starship(mainController, canvas.getWidth() / 2, canvas.getHeight() / 2);
        starship.setWorldWidth(canvas.getWidth());
        starship.setWorldHeight(canvas.getHeight());
        movingObjects.add(starship);

        // Create asteroid
        for (int i = 0; i < 10; i++) {
            movingObjects.add(createAsteroid());
        }
    }

    public void fire() {
        Shot shot = new Shot(starship.getX() + 23, starship.getY() + 25, starship.getDirection());
        shot.setWorldWidth(canvas.getWidth());
        shot.setWorldHeight(canvas.getHeight());
        movingObjects.add(shot);
    }

    protected Asteroid createAsteroid() {
        Asteroid result;
        int side = random.nextInt(4);
        Asteroid.SIZE size = Asteroid.SIZE.values()[random.nextInt(3)];
        switch (side) {
            case 0:
                result = new Asteroid(1,
                        random.nextInt((int)canvas.getHeight() - 10) + 5,
                        random.nextInt(90) + 45,
                        (random.nextDouble() * 6) + 0.25,
                        random.nextDouble(),
                        size);
                break;
            case 1:
                result = new Asteroid(random.nextInt((int)canvas.getWidth() - 10) + 5,
                        1,
                        random.nextInt(90) + 135,
                        (random.nextDouble() * 6) + 0.25,
                        random.nextDouble(),
                        size);
                break;
            case 2:
                result = new Asteroid(canvas.getWidth() - 1,
                        random.nextInt((int)canvas.getHeight() - 10) + 5,
                        random.nextInt(90) + 225,
                        (random.nextDouble() * 6) + 0.25,
                        random.nextDouble(),
                        size);
                break;
            default:
                result = new Asteroid(random.nextInt((int)canvas.getWidth() - 10) + 5,
                        canvas.getHeight() - 1,
                        (random.nextInt(90) + 305) % 360,
                        (random.nextDouble() * 6) + 0.25,
                        random.nextDouble(),
                        size);
                break;
        }
        result.setWorldWidth(canvas.getWidth());
        result.setWorldHeight(canvas.getHeight());

        return result;
    }

    @Override
    public void handle(long now) {
        if (showFrameRate) {
            long oldFrameTime = frameTimes[frameTimeIndex];
            frameTimes[frameTimeIndex] = now;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
            if (frameTimeIndex == 0) {
                arrayFilled = true;
            }

            if (arrayFilled) {
                long elapsedNanos = now - oldFrameTime;
                long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                GraphicsContext gc = mainController.statusCanvas.getGraphicsContext2D();
                gc.clearRect(mainController.statusCanvas.getWidth() - 150, mainController.statusCanvas.getHeight() - 75, 150, 50);
                gc.setFont(Font.font("Arial", 24));
                gc.setFill(Color.WHITE);
                gc.fillText(String.format("FR: %d", (int) frameRate), mainController.statusCanvas.getWidth() - 100, mainController.statusCanvas.getHeight() - 50);
            }
        }

        if (!isPaused()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (MovingObject o : movingObjects) {
                o.update();
            }

            List<MovingObject> toAdd = new ArrayList<>();
            for (MovingObject o : movingObjects) {
                toAdd.addAll(checkForCollisions(o));
            }

            int numRemoved = 0;
            Iterator<MovingObject> it = movingObjects.iterator();
            while (it.hasNext()) {
                MovingObject movingObject = it.next();
                if (movingObject.removeFromGame()) {
                    if (movingObject instanceof Asteroid) {
                        numRemoved++;
                    }
                    it.remove();
                }
            }

            for (MovingObject o : movingObjects) {
                o.draw(gc);
            }

            for (int i = 0; i < numRemoved; i++) {
                movingObjects.add(createAsteroid());
            }

            movingObjects.addAll(toAdd);

            GraphicsContext statusGc = mainController.statusCanvas.getGraphicsContext2D();
            statusGc.clearRect(25, 25, 400, 75);
            statusGc.setFill(Color.PURPLE);
            statusGc.setFont(Font.font("Arial", 24));
            statusGc.fillText(String.format("Score: %d             Energy: %d", starship.getScore(), starship.getFuel()), 50, 50);
        }
    }

    protected List<MovingObject> checkForCollisions(MovingObject m) {
        List<MovingObject> result = new ArrayList<>();

        for (MovingObject movingObject : movingObjects) {
            if (movingObject != m) {
                if ((m instanceof Shot && movingObject == starship) || (m == starship && movingObject instanceof Shot)) {
                    // do nothing
                } else {
                    if (movingObject.getBounds().intersects(m.getBounds())) {
                        if (m == starship && movingObject instanceof FuelBonus) {
                            movingObject.setDestroyed(true);
                            starship.setFuel(starship.getFuel() + ((FuelBonus)movingObject).getValue());
                        } else if (!(m instanceof FuelBonus)){
                            m.setDestroyed(true);
                            if (movingObject instanceof Shot) {
                                starship.setScore(starship.getScore() + 50);
                                if (random.nextInt(100) < 35) {
                                    FuelBonus fuelBonus = new FuelBonus(3500, 1000, m.getX(), m.getY());
                                    fuelBonus.setWorldWidth(canvas.getWidth());
                                    fuelBonus.setWorldHeight(canvas.getHeight());
                                    result.add(fuelBonus);
                                }
                            } else if (m == starship || movingObject == starship) {
                                starship.wasHit();
                            }
                        }
                        break;
                    }
                }
            }
        }

        return result;
    }

    public void center() {
        starship.setX(canvas.getWidth() / 2);
        starship.setY(canvas.getHeight() / 2);
    }

    public void goRight() {
        starship.setRightPressed(true);
    }

    public void goLeft() {
        starship.setLeftPressed(true);
    }

    public void accelerate() {
        starship.setAccelerate(true);
    }

    public void stopRight() {
        starship.setRightPressed(false);
    }

    public void stopLeft() {
        starship.setLeftPressed(false);
    }

    public void stopAccelerate() {
        starship.setAccelerate(false);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            GraphicsContext gc = mainController.statusCanvas.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 72));
            gc.fillText("Paused", (mainController.statusCanvas.getWidth() / 2) - 50, mainController.statusCanvas.getHeight() / 2);
        } else {
            GraphicsContext gc = mainController.statusCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, mainController.statusCanvas.getWidth(), mainController.statusCanvas.getHeight());
        }
    }

    public boolean isShowFrameRate() {
        return showFrameRate;
    }

    public void setShowFrameRate(boolean showFrameRate) {
        this.showFrameRate = showFrameRate;
        if (showFrameRate) {
            frameTimeIndex = 0;
            arrayFilled = false;
        } else {
            GraphicsContext gc = mainController.statusCanvas.getGraphicsContext2D();
            gc.clearRect(mainController.statusCanvas.getWidth() - 150, mainController.statusCanvas.getHeight() - 75, 150, 50);
        }
    }
}
