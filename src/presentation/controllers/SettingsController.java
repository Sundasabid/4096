package presentation.controllers;


import app.AppContext;
import app.AppContextAware;
import domain.model.GameContext;
import domain.model.Reset;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import presentation.service.NavigationService;

import java.io.IOException;

public class SettingsController implements AppContextAware {

    private AppContext ctx;
    private NavigationService navigation;
    private GameContext gameContext;
    private String previousScreen = "/view/mainmenu.fxml";

    @FXML private ToggleButton vibrationToggle;
    @FXML private ToggleButton soundToggle;

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
        if (this.gameContext == null && ctx != null) {
            this.gameContext = ctx.gameContext;
        }
    }

    public void setPreviousScreen(String screenPath) {
        this.previousScreen = screenPath;
    }

    @FXML
    public void initialize() {
        setupToggle(vibrationToggle);
        setupToggle(soundToggle);
    }

    private void setupToggle(ToggleButton toggle) {
        StackPane pane = (StackPane) toggle.getGraphic();
        Rectangle track = (Rectangle) pane.getChildren().get(0);
        Circle circle = (Circle) pane.getChildren().get(1);

        toggle.setOnAction(e -> {
            TranslateTransition move = new TranslateTransition();
            move.setDuration(Duration.millis(200));
            move.setNode(circle);

            if (toggle.isSelected()) {
                move.setToX(17.5);
                track.setFill(Color.rgb(76, 175, 80));
            } else {
                move.setToX(-17.5);
                track.setFill(Color.rgb(204, 204, 204));
            }

            move.play();
        });
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.navigateTo(previousScreen);
    }

    @FXML
    private void handleResetButton(ActionEvent event) {
        try {
            Stage settingsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            GaussianBlur blur = new GaussianBlur(10);
            settingsStage.getScene().getRoot().setEffect(blur);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/resetbox.fxml"));
            StackPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.centerOnScreen();

            ResetDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            settingsStage.getScene().getRoot().setEffect(null);

            if (!controller.isResetConfirmed()) {
                return;
            }

            if (gameContext == null && ctx != null) {
                gameContext = ctx.gameContext;
            }

            Reset reset = new Reset();
            reset.resetGame(gameContext);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHowToPlay(ActionEvent event) {
        try {
            Stage settingsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            GaussianBlur blur = new GaussianBlur(10);
            settingsStage.getScene().getRoot().setEffect(blur);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/howtoplay.fxml"));
            StackPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(page);
            scene.setFill(Color.TRANSPARENT);
            dialogStage.setScene(scene);

            dialogStage.setX((settingsStage.getX() + settingsStage.getWidth() / 2) - 180);
            dialogStage.setY((settingsStage.getY() + settingsStage.getHeight() / 2) - 160);

            HowToPlayController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            settingsStage.getScene().getRoot().setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}