package presentation.controllers;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class HowToPlayController {

    @FXML
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}