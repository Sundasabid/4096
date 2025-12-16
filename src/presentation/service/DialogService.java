package presentation.service;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import presentation.controllers.SoundManager;

public class DialogService {
    private final Stage owner;

    public DialogService(Stage owner) {
        this.owner = owner;
    }

    public void showError(String title, String message) {
        showStyledDialog(title, message);
    }

    public void showInfo(String title, String message) {
        showStyledDialog(title, message);
    }

    private void showStyledDialog(String title, String message) {
        SoundManager.error();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(owner);

        VBox content = new VBox(18);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25, 30, 25, 30));
        content.setMaxWidth(260);
        content.setMaxHeight(180);
        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(25, 15, 45, 0.98), rgba(15, 8, 30, 0.98));" +
                        "-fx-border-color: linear-gradient(to right, #4ECDC4, #FF6B9D);" +
                        "-fx-border-width: 3;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(78, 205, 196, 0.4), 25, 0, 0, 0);"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: normal;" +
                        "-fx-text-alignment: center;" +
                        "-fx-wrap-text: true;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(200);

        Button okButton = new Button("OK");
        okButton.setPrefWidth(160);
        okButton.setPrefHeight(40);
        okButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #4ECDC4, #44A08D);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(78, 205, 196, 0.4), 10, 0, 0, 0);"
        );
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(messageLabel, okButton);

        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.sizeToScene();

        dialogStage.setX(owner.getX() + (owner.getWidth() - 260) / 2);
        dialogStage.setY(owner.getY() + (owner.getHeight() - 180) / 2);

        dialogStage.showAndWait();
    }
}