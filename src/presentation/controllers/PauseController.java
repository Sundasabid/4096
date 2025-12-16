package presentation.controllers;

import app.AppContext;
import app.AppContextAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import presentation.service.NavigationService;

public class PauseController implements AppContextAware {
    private AppContext ctx;
    private NavigationService navigation;

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
    }

    @FXML
    private void onPlay(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.goToGameBoard();
    }

    @FXML
    private void onMainMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.goToMainMenu();
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigation = new NavigationService(stage, ctx);
        navigation.goToGameBoard();
    }
}