package presentation.controllers;

import app.AppContext;
import app.AppContextAware;
import domain.model.Position;
import domain.model.Tile;
import domain.service.GameService;
import domain.service.GameServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import presentation.service.DialogService;
import presentation.service.NavigationService;
import presentation.view.*;
import presentation.viewmodel.GameBoardViewModel;

import java.util.ArrayList;
import java.util.List;

public class GameBoardController implements AppContextAware {

    @FXML private Label diamondsLabel;
    @FXML private Label bestTileButton;
    @FXML private GridPane gameGrid;
    @FXML private Pane overlayPane;

    private enum InputMode {
        MERGE,
        SHUFFLE_TWO,
        NONE
    }

    private InputMode inputMode = InputMode.MERGE;

    private AppContext ctx;
    private GameService gameService;
    private GameBoardViewModel viewModel;
    private NavigationService navigation;
    private DialogService dialogs;

    private TileRenderer tileRenderer;
    private InputHandler inputHandler;
    private DragLineDrawer dragLineDrawer;
    private MergeAnimator animator;

    private StackPane[][] tileNodes;
    private final List<Position> shuffleTwoSelected = new ArrayList<>(2);

    @FXML
    private void initialize() {
        if (gameGrid != null) {
            gameGrid.setOnMouseReleased(e -> {
                if (inputMode == InputMode.MERGE && viewModel != null && viewModel.isDragging()) {
                    finishDragAndMerge();
                    e.consume();
                }
            });
        }
    }

    @Override
    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;

        this.gameService = new GameServiceImpl(
                ctx.gameContext,
                ctx.mergeValidator,
                ctx.mergeLogic,
                ctx.gameEnd,
                ctx.repository,
                ctx.shuffleAllPowerUp,
                ctx.shuffleTwoPowerUp,
                ctx.undoPowerUp
        );

        this.viewModel = new GameBoardViewModel(gameService);

        Stage stage = (Stage) gameGrid.getScene().getWindow();
        this.navigation = new NavigationService(stage, ctx);
        this.dialogs = new DialogService(stage);

        this.tileRenderer = new TileRenderer();
        this.inputHandler = new InputHandler();
        this.animator = new MergeAnimator();

        setupInputHandlers();
        renderUI();
    }

    private void setupInputHandlers() {
        inputHandler.setOnTilePressed(this::onTilePressed);
        inputHandler.setOnTileDragged(this::onTileDragged);
        inputHandler.setOnDragFinished(this::finishDragAndMerge);
    }

    @FXML
    private void handlePauseButton(ActionEvent event) {
        navigation.goToPause();
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        navigation.goToSettings();
    }

    @FXML
    private void handleUndoButton(ActionEvent event) {
        if (gameService.undoLastMove()) {
            renderUI();
        } else {
            dialogs.showError("Undo Not Available", "No undo available or not enough diamonds.");
        }
    }

    @FXML
    private void handleShuffleTwoButton(ActionEvent event) {
        if (!ctx.shuffleTwoPowerUp.canActivate()) {
            dialogs.showError("Not Enough Diamonds", "You need more diamonds to use Shuffle Two.");
            return;
        }

        inputMode = InputMode.SHUFFLE_TWO;
        shuffleTwoSelected.clear();
        dialogs.showInfo("Shuffle Two", "Click TWO tiles to swap them.");
    }

    @FXML
    private void handleShuffleAllButton(ActionEvent event) {
        if (gameService.shuffleAll()) {
            gameService.captureSnapshot();
            renderUI();
        } else {
            dialogs.showError("Not Enough Diamonds", "You need more diamonds to use Shuffle All.");
        }
    }

    void renderUI() {
        if (gameGrid == null || ctx == null) return;

        Tile[][] grid = ctx.gameContext.gameBoard().getGrid();
        tileNodes = tileRenderer.renderGrid(gameGrid, grid);

        attachInputToTiles();

        this.dragLineDrawer = new DragLineDrawer(overlayPane, gameGrid, tileNodes);

        bestTileButton.setText(String.valueOf(ctx.gameContext.player().getBestTile()));
        diamondsLabel.setText(String.valueOf(ctx.gameContext.player().getDiamonds()));
    }

    private void attachInputToTiles() {
        Tile[][] grid = ctx.gameContext.gameBoard().getGrid();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Position pos = new Position(row, col);
                StackPane tile = tileNodes[row][col];

                if (inputMode == InputMode.SHUFFLE_TWO) {
                    inputHandler.attachClickHandler(tile, pos, this::onTileClickedForShuffle);
                } else {
                    inputHandler.attachMergeHandlers(tile, pos);
                }
            }
        }
    }

    private void onTileClickedForShuffle(Position position) {
        SoundManager.click();

        for (Position selected : shuffleTwoSelected) {
            if (selected.equals(position)) return;
        }

        shuffleTwoSelected.add(position);

        if (shuffleTwoSelected.size() == 2) {
            gameService.captureSnapshot();
            gameService.shuffleTwo(shuffleTwoSelected.get(0), shuffleTwoSelected.get(1));

            shuffleTwoSelected.clear();
            renderUI();
            inputMode = InputMode.MERGE;
        }
    }

    private void onTilePressed(Position position) {
        viewModel.clearSelection();
        tileRenderer.clearHighlights(tileNodes);
        if (dragLineDrawer != null) dragLineDrawer.clearLine();

        SoundManager.click();
        viewModel.selectPosition(position);
        viewModel.startDragging();

        tileRenderer.highlightTile(tileNodes[position.row()][position.col()]);
        if (dragLineDrawer != null) dragLineDrawer.startLine(position);
    }

    private void onTileDragged(Position position) {
        if (!viewModel.isDragging()) return;

        if (viewModel.canAddPosition(position)) {
            SoundManager.select();
            viewModel.selectPosition(position);

            tileRenderer.highlightTile(tileNodes[position.row()][position.col()]);
            if (dragLineDrawer != null) dragLineDrawer.extendLine(position);
        }
    }

    private void finishDragAndMerge() {
        if (!viewModel.isDragging()) return;

        List<Position> path = viewModel.getSelectedPositions();

        if (path.size() < 2) {
            if (dragLineDrawer != null) dragLineDrawer.clearLine();
            tileRenderer.clearHighlights(tileNodes);
            viewModel.clearSelection();
            return;
        }

        animator.playMergeAnimation(tileNodes, path, () -> {
            viewModel.executeMerge();
            SoundManager.merge();
            renderUI();
            if (gameService.isGameOver()) {
                dialogs.showError("Game Over", "No more moves left!");
            }
        });

        if (dragLineDrawer != null) dragLineDrawer.clearLine();
        tileRenderer.clearHighlights(tileNodes);
        viewModel.clearSelection();
    }
}