package presentation.viewmodel;

import domain.model.Position;
import domain.service.GameService;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class GameBoardViewModel {
    private final GameService gameService;
    private final IntegerProperty diamonds = new SimpleIntegerProperty();
    private final IntegerProperty bestTile = new SimpleIntegerProperty();
    private final List<Position> selectedPositions = new ArrayList<>();
    private boolean isDragging = false;

    public GameBoardViewModel(GameService gameService) {
        this.gameService = gameService;
        updateProperties();
    }

    public void selectPosition(Position pos) {
        selectedPositions.add(pos);
    }

    public void clearSelection() {
        selectedPositions.clear();
        isDragging = false;
    }

    public boolean canAddPosition(Position pos) {
        for (Position selected : selectedPositions) {
            if (selected.equals(pos)) return false;
        }

        List<Position> testList = new ArrayList<>(selectedPositions);
        testList.add(pos);
        return gameService.canMerge(testList);
    }

    public void executeMerge() {
        if (selectedPositions.size() >= 2) {
            gameService.executeMerge(selectedPositions);
            clearSelection();
            updateProperties();
        }
    }

    public void startDragging() {
        isDragging = true;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public List<Position> getSelectedPositions() {
        return new ArrayList<>(selectedPositions);
    }

    public void updateProperties() {
        diamonds.set(gameService.getDiamonds());
        bestTile.set(gameService.getBestTile());
    }

    public IntegerProperty diamondsProperty() { return diamonds; }
    public IntegerProperty bestTileProperty() { return bestTile; }
}