package controllers;

import Model.*;
import app.AppContext;
import app.AppContextAware;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * Game board controller with 2248-like animations:
 * - Press: selected tile becomes white
 * - Drag across valid merge cells: white path line + selected tiles become white
 * - Release: merge with a small fade/pop animation
 */
public class GameBoardController implements AppContextAware {

    @FXML private Label diamondsLabel;
    @FXML private Label bestTileButton;

    @FXML private GridPane gameGrid;
    @FXML private Pane overlayPane; // (added in gameboard.fxml) to draw the drag line

    private enum InputMode {
        MERGE,
        SHUFFLE_TWO,
        NONE
    }

    private InputMode inputMode = InputMode.MERGE;

    // ====== Injected via AppContext ======
    private AppContext ctx;
    private GameEnd gameEnd;
    private Repository repository;
    private ShuffleAllPowerUp shuffleAllPowerUp;
    private GameContext gameContext;
    private ShuffleTwoPowerUp shuffleTwoPowerUp;
    private MergeLogic mergeLogic;
    private UndoPowerUp undoPowerUp;
    private MergeValidator mergeValidator;

    // ====== Selection state ======
    private final ArrayList<int[]> selectedCells = new ArrayList<>();
    private boolean isDragging = false;

    // ====== Shuffle Two state ======
    private final List<int[]> shuffleTwoSelected = new ArrayList<>(2);

    // ====== Tile node references (for CSS + animation) ======
    private StackPane[][] tileNodes;

    // ====== Drag-line ======
    private Polyline dragLine;

    // FXMLLoader calls initialize() BEFORE setAppContext(...)
    @FXML
    private void initialize() {
        // Ensure release works even if user releases outside a tile
        if (gameGrid != null) {
            gameGrid.setOnMouseReleased(e -> {
                if (inputMode == InputMode.MERGE && isDragging) {
                    finishDragAndMerge();
                    e.consume();
                }
            });
        }
    }

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;

        // Map from context to your existing fields
        this.repository = ctx.repository;
        this.shuffleAllPowerUp = ctx.shuffleAllPowerUp;
        this.gameContext = ctx.gameContext;
        this.shuffleTwoPowerUp = ctx.shuffleTwoPowerUp;
        this.mergeLogic = ctx.mergeLogic;
        this.mergeValidator = ctx.mergeValidator;
        this.gameEnd = ctx.gameEnd;
        this.undoPowerUp = ctx.undoPowerUp;

        // Render when context arrives
        renderUI();
    }

    // -------------------------
    // Buttons
    // -------------------------

    @FXML
    private void handlePauseButton(ActionEvent event) {
        loadScreenWithInjection(event, "/view/pause.fxml", controller -> {
            // nothing extra for now
        });
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        loadScreenWithInjection(event, "/view/settings.fxml", controller -> {
            if (controller instanceof SettingsController sc) {
                sc.setGameBoardController(this, gameContext);
                sc.setPreviousScreen("/view/gameboard.fxml");
            }
        });
    }

    @FXML
    private void handleUndoButton(ActionEvent event) {
        if (repository == null || undoPowerUp == null) {
            System.err.println("ERROR: AppContext not injected (undoPowerUp/repository is null).");
            return;
        }

        boolean success = undoPowerUp.activate();

        if (success) {
            renderUI();
        } else {
            showStyledDialog("Undo Not Available", "No undo available or not enough diamonds.");
        }
    }

    @FXML
    private void handleShuffleTwoButton(ActionEvent event) {
        if (shuffleTwoPowerUp == null) {
            System.err.println("ERROR: AppContext not injected (shuffleTwoPowerUp is null).");
            return;
        }

        if (!shuffleTwoPowerUp.canActivate()) {
            showStyledDialog("Not Enough Diamonds", "You need more diamonds to use Shuffle Two.");
            return;
        }

        inputMode = InputMode.SHUFFLE_TWO;
        shuffleTwoSelected.clear();

        showStyledDialog("Shuffle Two", "Click TWO tiles to swap them.");
    }

    @FXML
    private void handleShuffleAllButton(ActionEvent event) {
        if (repository == null || shuffleAllPowerUp == null) {
            System.err.println("ERROR: AppContext not injected (shuffleAllPowerUp/repository is null).");
            return;
        }

        if (shuffleAllPowerUp.activate()) {
            repository.captureSnapshot();
            renderUI();
        } else {
            showStyledDialog("Not Enough Diamonds", "You need more diamonds to use Shuffle All.");
        }
    }

    private void showStyledDialog(String title, String message) {
        SoundManager.error();

        // Create custom dialog stage
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(gameGrid.getScene().getWindow());

        // Create content
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

        // Message label
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

        // OK button
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

        // NO outer dark background - direct content only
        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.sizeToScene();

        Stage parentStage = (Stage) gameGrid.getScene().getWindow();
        dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - 260) / 2);
        dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - 180) / 2);

        dialogStage.showAndWait();
    }
    // -------------------------
    // Rendering + Input
    // -------------------------

    void renderUI() {

        if (gameGrid == null) return;
        if (gameContext == null) {
            System.err.println("renderUI(): gameContext is null (AppContext not injected yet).");
            return;
        }

        gameGrid.getChildren().clear();

        Tile[][] grid = gameContext.gameBoard().getGrid();
        tileNodes = new StackPane[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {

                int value = grid[row][col].getValue();

                StackPane tile = new StackPane();
                tile.setPickOnBounds(true);

                tile.setMinSize(54, 54);
                tile.setPrefSize(54, 54);
                tile.setMaxSize(54, 54);

                tile.getStyleClass().add(value == 0 ? "tile-0" : "tile-" + value);

                Label label = new Label(value == 0 ? "" : String.valueOf(value));
                label.setFont(new Font(22));
                label.setTextFill(Color.WHITE);
                label.setStyle("-fx-font-weight: bold;");
                label.setMouseTransparent(true);
                tile.getChildren().add(label);

                final int r = row;
                final int c = col;

                // Save reference for highlight/animation
                tileNodes[r][c] = tile;

                // -------------------------
                // Shuffle Two click mode
                // -------------------------
                tile.setOnMouseClicked(e -> {
                    if (inputMode == InputMode.SHUFFLE_TWO) {
                        onTileClicked(r, c);
                        e.consume();
                    }
                });

                // -------------------------
                // Merge mode (drag)
                // -------------------------
                tile.setOnMousePressed(e -> {
                    if (inputMode != InputMode.MERGE) return;
                    onTilePressed(r, c);
                    e.consume();
                });

                tile.setOnDragDetected(e -> {
                    if (inputMode != InputMode.MERGE) return;

                    isDragging = true;
                    tile.startFullDrag();
                    e.consume();
                });

                tile.setOnMouseDragEntered(e -> {
                    if (inputMode != InputMode.MERGE) return;
                    if (!isDragging) return;
                    tryAddCellWhileDragging(r, c);
                    e.consume();
                });

                tile.setOnMouseReleased(e -> {
                    if (inputMode != InputMode.MERGE) return;
                    if (!isDragging) return;
                    finishDragAndMerge();
                    e.consume();
                });

                gameGrid.add(tile, col, row);
            }
        }

        bestTileButton.setText(String.valueOf(gameContext.player().getBestTile()));
        diamondsLabel.setText(String.valueOf(gameContext.player().getDiamonds()));
    }

    private void onTileClicked(int row, int col) {
        SoundManager.click();
        Tile[][] g = gameContext.gameBoard().getGrid();
        if (row < 0 || row >= g.length || col < 0 || col >= g[0].length) {
            return;
        }

        // prevent selecting same cell twice
        for (int[] pos : shuffleTwoSelected) {
            if (pos[0] == row && pos[1] == col) return;
        }

        shuffleTwoSelected.add(new int[]{row, col});

        // if 2 selected, apply powerup
        if (shuffleTwoSelected.size() == 2) {
            int[][] positions = new int[2][2];
            positions[0] = shuffleTwoSelected.get(0);
            positions[1] = shuffleTwoSelected.get(1);

            repository.captureSnapshot();
            shuffleTwoPowerUp.activate(positions);

            shuffleTwoSelected.clear();
            renderUI();
            inputMode = InputMode.MERGE;
        }
    }

    // -------------------------
    // 2248-like merge input
    // -------------------------

    private void onTilePressed(int row, int col) {
        if (repository == null || mergeLogic == null || mergeValidator == null) {
            System.err.println("ERROR: AppContext not injected (merge components are null).");
            return;
        }

        selectedCells.clear();
        clearHighlights();
        clearDragLine();

        SoundManager.click();
        selectedCells.add(new int[]{row, col});
        highlightCell(row, col);
        startDragLineAt(row, col);
    }

    private void tryAddCellWhileDragging(int row, int col) {
        if (!isDragging) return;

        boolean ok = mergeValidator.canAddCell(selectedCells, row, col);
        if (ok) {
            SoundManager.select();
            selectedCells.add(new int[]{row, col});

            highlightCell(row, col);
            extendDragLineTo(row, col);
        }
    }

    private void finishDragAndMerge() {
        isDragging = false;

        if (selectedCells.size() < 2) {
            clearDragLine();
            clearHighlights();
            selectedCells.clear();
            return;
        }

        // Copy path because we'll clear selection before/after animations
        ArrayList<int[]> path = new ArrayList<>(selectedCells);

        playMergeAnimation(path, () -> {
            repository.captureSnapshot();
            mergeLogic.activate(path);
            SoundManager.merge();
            renderUI();

            if (gameEnd != null && gameEnd.activate()) {
                showGameOverDialog();
            }
        });

        clearDragLine();
        clearHighlights();
        selectedCells.clear();
    }

    // -------------------------
    // Highlight helpers
    // -------------------------

    private void highlightCell(int row, int col) {
        StackPane node = tileNodes[row][col];
        if (node != null && !node.getStyleClass().contains("tile-selected")) {
            node.getStyleClass().add("tile-selected");
        }
    }

    private void clearHighlights() {
        if (tileNodes == null) return;
        for (int r = 0; r < tileNodes.length; r++) {
            for (int c = 0; c < tileNodes[r].length; c++) {
                StackPane node = tileNodes[r][c];
                if (node != null) node.getStyleClass().remove("tile-selected");
            }
        }
    }

    // -------------------------
    // Drag line helpers
    // -------------------------

    private void startDragLineAt(int row, int col) {
        if (overlayPane == null) return;

        overlayPane.getChildren().clear();

        dragLine = new Polyline();
        dragLine.setMouseTransparent(true);
        dragLine.setStroke(Color.WHITE);
        dragLine.setStrokeWidth(8);
        dragLine.setStrokeLineCap(StrokeLineCap.ROUND);
        dragLine.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // IMPORTANT: point is the GRID CELL center (stable), not the tile's visual center
        Point2D p = cellCenterInOverlay(row, col);
        dragLine.getPoints().addAll(p.getX(), p.getY());
        overlayPane.getChildren().add(dragLine);
    }

    private void extendDragLineTo(int row, int col) {
        if (dragLine == null) return;

        // IMPORTANT: next point is the GRID CELL center
        Point2D p = cellCenterInOverlay(row, col);
        dragLine.getPoints().addAll(p.getX(), p.getY());
    }

    private void clearDragLine() {
        if (overlayPane != null) overlayPane.getChildren().clear();
        dragLine = null;
    }

    /**
     * Returns the CENTER of the GridPane CELL at (row,col), not the scaled tile bounds center.
     * This keeps the line perfectly centered even when tiles scale (tile-selected effect).
     */
    private Point2D cellCenterInOverlay(int row, int col) {
        if (overlayPane == null || gameGrid == null || tileNodes == null) return new Point2D(0, 0);

        StackPane tile = tileNodes[row][col];
        if (tile == null) return new Point2D(0, 0);

        // Position of the tile within the GridPane (layout coords are stable even if node scales)
        double w = tile.getLayoutBounds().getWidth();
        double h = tile.getLayoutBounds().getHeight();

        double xInGrid = tile.getLayoutX() + w / 2.0;
        double yInGrid = tile.getLayoutY() + h / 2.0;

        // Grid local -> Scene -> Overlay local
        Point2D scenePoint = gameGrid.localToScene(xInGrid, yInGrid);
        return overlayPane.sceneToLocal(scenePoint);
    }

    // -------------------------
    // Merge animation
    // -------------------------

    private void playMergeAnimation(ArrayList<int[]> path, Runnable after) {
        if (tileNodes == null || path.isEmpty()) {
            after.run();
            return;
        }

        ParallelTransition disappear = new ParallelTransition();

        // Fade/scale all EXCEPT last tile
        for (int i = 0; i < path.size() - 1; i++) {
            int r = path.get(i)[0];
            int c = path.get(i)[1];
            StackPane n = tileNodes[r][c];
            if (n == null) continue;

            FadeTransition ft = new FadeTransition(Duration.millis(140), n);
            ft.setToValue(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(140), n);
            st.setToX(0.6);
            st.setToY(0.6);

            disappear.getChildren().add(new ParallelTransition(ft, st));
        }

        int[] last = path.get(path.size() - 1);
        StackPane lastNode = tileNodes[last[0]][last[1]];

        ScaleTransition pop = new ScaleTransition(Duration.millis(160), lastNode);
        pop.setToX(1.12);
        pop.setToY(1.12);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);

        SequentialTransition seq = new SequentialTransition(disappear, pop);
        seq.setOnFinished(e -> after.run());
        seq.play();
    }

    private void showGameOverDialog() {
        showStyledDialog("Game Over", "No more moves left!");
    }

    // -------------------------
    // Safe screen loading (injects AppContext)
    // -------------------------

    private interface ControllerConfigurer {
        void configure(Object controller);
    }

    private void loadScreenWithInjection(ActionEvent event, String fxmlPath, ControllerConfigurer configurer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            // Inject shared context if controller supports it
            if (controller instanceof AppContextAware aware && ctx != null) {
                aware.setAppContext(ctx);
            }

            // Extra wiring for special controllers (Settings etc.)
            if (configurer != null) {
                configurer.configure(controller);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}