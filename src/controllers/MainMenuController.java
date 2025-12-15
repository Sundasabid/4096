package controllers;

import app.AppContext;
import app.AppContextAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController implements AppContextAware {
    @FXML
    private Label bestTileButton;
    @FXML
    private Label diamondsLabel;

    private AppContext ctx;

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
        if (bestTileButton != null && ctx != null && ctx.gameContext != null) {
            bestTileButton.setText(String.valueOf(ctx.gameContext.player().getBestTile()));
            diamondsLabel.setText(
                    String.valueOf(ctx.gameContext.player().getDiamonds())
            );
        }


    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        // for now: just go to settings (we’ll add “back” properly next)
        new app.Navigator((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow(), ctx)
                .go("/view/settings.fxml");
    }

    @FXML
    private void handlePlayButton(ActionEvent event) {
        new app.Navigator((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow(), ctx)
                .go("/view/gameboard.fxml");
    }
}
