package presentation.service;

import app.AppContext;
import app.AppContextAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {
    private final Stage stage;
    private final AppContext context;

    public NavigationService(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AppContextAware aware) {
                aware.setAppContext(context);
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + fxmlPath, e);
        }
    }

    public void goToMainMenu() {
        navigateTo("/view/mainmenu.fxml");
    }

    public void goToGameBoard() {
        navigateTo("/view/gameboard.fxml");
    }

    public void goToSettings() {
        navigateTo("/view/settings.fxml");
    }

    public void goToPause() {
        navigateTo("/view/pause.fxml");
    }
}