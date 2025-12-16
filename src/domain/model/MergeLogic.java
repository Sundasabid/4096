package domain.model;

import domain.model.GameContext;
import domain.model.SpawnLogic;
import domain.model.Tile;

import java.util.ArrayList;

public class MergeLogic {
    private final GameContext gameContext;
    private final SpawnLogic spawnLogic;
    private Tile[][] grid;

    public MergeLogic(GameContext gameContext, SpawnLogic spawnLogic) {
        this.gameContext = gameContext;
        this.spawnLogic = spawnLogic;
    }

    public void activate(ArrayList<int[]> arrayList) {
        grid = gameContext.gameBoard().getGrid();
        int[][] array = arrayList.toArray(new int[arrayList.size()][]);
        deleteTiles(array);
        shiftTiles();
        spawnNewTiles();
    }

    private void deleteTiles(int[][] array) {
        int i;
        int total = 0;

        for (i = 0; i < array.length; i++) {
            total += grid[array[i][0]][array[i][1]].getValue();
            if (i != array.length - 1) {
                grid[array[i][0]][array[i][1]] = null;
            }
        }

        int finalValue = calculateMergeValue(total);
        grid[array[i - 1][0]][array[i - 1][1]].setValue(finalValue);

        PlayerSession player = gameContext.player();
        if (finalValue > player.getBestTile()) {
            player.updateBestTile(finalValue);
        }
        gameContext.player().addScore(array.length);
    }

    private int calculateMergeValue(int total) {
        int value = 2;
        while (value < total) {
            value *= 2;
        }
        return value / 2;
    }

    private void shiftTiles() {
        int cols = gameContext.gameBoard().getCol();
        int rows = gameContext.gameBoard().getRow();

        for (int counter = 0; counter < rows - 1; counter++) {
            for (int j = cols - 1; j >= 0; j--) {
                for (int i = rows - 1; i >= 0; i--) {
                    if (grid[i][j] == null) {
                        if (i != 0) {
                            grid[i][j] = grid[i - 1][j];
                            grid[i - 1][j] = null;
                        }
                    }
                }
            }
        }
        gameContext.gameBoard().setGrid(grid);
    }

    private void spawnNewTiles() {
        GameBoard gameBoard = gameContext.gameBoard();
        for (int i = 0; i < gameBoard.getRow(); i++) {
            for (int j = 0; j < gameBoard.getCol(); j++) {
                if (gameBoard.getGrid()[i][j] == null) {
                    Tile tile = spawnLogic.spawnTile(i,j,gameContext.player().getBestTile());
                    gameBoard.getGrid()[i][j] = tile;
                }
            }
        }
    }
}