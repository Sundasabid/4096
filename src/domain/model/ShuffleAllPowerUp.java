package domain.model;

import java.util.ArrayList;
import java.util.Collections;

public class ShuffleAllPowerUp extends PowerUp{

    public ShuffleAllPowerUp(GameContext g) {
        super(g);this.cost=3;
    }
    public boolean activate(){

        if(canActivate()){
            Tile[][] grid = gameContext.gameBoard().getGrid();
            int rows = grid.length,cols = grid[0].length;
            ArrayList<Integer> values = new ArrayList<>(rows * cols);
            for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++)
                values.add(grid[r][c].getValue());
            Collections.shuffle(values);

            int index = 0;
            for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++)
            {   grid[r][c].setValue(values.get(index));
                index++;
            }
            gameContext.gameBoard().setGrid(grid);
useScore();
            return true;
        }return false;
    }
}
