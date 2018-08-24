package org.culpan.starbattle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.culpan.starbattle.controllers.MainController;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setStage(stage);

        final Scene scene = new Scene(root);

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            mainController.handleResize();
        });

        stage.setFullScreen(true);
        stage.setTitle("Star Battle");
        stage.setScene(scene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();

        final Animator animator = new Animator(mainController);
        animator.setupGame();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case Q:
                    stage.close();
                    break;
                case C:
                    animator.center();
                    break;
                case ESCAPE:
                    animator.setPaused(!animator.isPaused());
                    break;
                case F:
                    animator.setShowFrameRate(!animator.isShowFrameRate());
                    break;
                case SPACE:
                    animator.fire();
                    break;
                case UP:  animator.accelerate(); break;
                case LEFT:  animator.goLeft(); break;
                case RIGHT: animator.goRight(); break;
                case SHIFT: animator.accelerate(); break;
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case UP:  animator.stopAccelerate(); break;
                case LEFT:  animator.stopLeft(); break;
                case RIGHT: animator.stopRight(); break;
                case SHIFT: animator.stopAccelerate(); break;
            }
        });
        animator.start();
    }

    public static void main(String [] args) {
        launch(args);
    }
}
