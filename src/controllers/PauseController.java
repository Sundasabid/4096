package controllers;

import app.AppContext;
import app.AppContextAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PauseController implements AppContextAware {
    private AppContext ctx;

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
    }

    @FXML
    private void onPlay(ActionEvent event) {
        new app.Navigator((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow(), ctx)
                .go("/view/gameboard.fxml");
    }

    @FXML
    private void onMainMenu(ActionEvent event) {
        new app.Navigator((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow(), ctx)
                .go("/view/mainmenu.fxml");
    }

    @FXML
    private void onClose(ActionEvent event) {
        new app.Navigator((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow(), ctx)
                .go("/view/gameboard.fxml");
    }
}
