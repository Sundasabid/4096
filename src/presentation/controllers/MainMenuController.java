package presentation.controllers;

import app.AppContext;
import app.AppContextAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import presentation.service.NavigationService;

public class MainMenuController implements AppContextAware {
    @FXML private Label bestTileButton;
    @FXML private Label diamondsLabel;

    private AppContext ctx;
    private NavigationService navigation;

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;

        if (bestTileButton != null && ctx != null && ctx.gameContext != null) {
            bestTileButton.setText(String.valueOf(ctx.gameContext.player().getBestTile()));
            diamondsLabel.setText(String.valueOf(ctx.gameContext.player().getDiamonds()));
        }
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.goToSettings();
    }

    @FXML
    private void handlePlayButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.goToGameBoard();
    }
}