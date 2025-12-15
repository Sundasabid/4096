package controllers;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ResetDialogController {

    @FXML
    private Stage dialogStage;
    private boolean resetConfirmed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleCancel() {
        resetConfirmed = false;
        dialogStage.close();
    }

    @FXML
    private void handleReset() {
        resetConfirmed = true;
        dialogStage.close();
    }

    public boolean isResetConfirmed() {
        return resetConfirmed;
    }
}