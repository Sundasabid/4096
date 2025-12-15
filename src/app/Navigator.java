package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class Navigator {
    private final Stage stage;
    private final AppContext ctx;

    public Navigator(Stage stage, AppContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
    }

    public void go(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AppContextAware aware) {
                aware.setAppContext(ctx);
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + fxmlPath, e);
        }
    }

    public AppContext ctx() { return ctx; }
    public Stage stage() { return stage; }
}
