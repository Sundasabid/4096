import app.AppContext;
import app.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        AppContext ctx = new AppContext();
        Navigator nav = new Navigator(stage, ctx);

        stage.setTitle("2248 Game");
        stage.setWidth(400);
        stage.setHeight(700);
        stage.setMinWidth(350);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.setResizable(true);

        // Start screen:
        nav.go("/presentation/view/mainmenu.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
