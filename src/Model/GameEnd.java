package Model;

public class GameEnd {
    GameBoard gameBoard;

    public GameEnd(GameBoard gameBoard) {
        this.gameBoard = gameBoard;}

    public boolean activate(){
        if(hasAnyMoves())return false;
        return true;
    }
    private boolean hasAnyMoves() {
        Tile[][] grid = gameBoard.getGrid();
        int rows = grid.length,cols = grid[0].length;

        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) {

            Tile current = grid[r][c];

            if (current == null) continue;
            int value = current.getValue();

            // check right neighbor
            if (c + 1 < cols)
            {Tile right = grid[r][c + 1];
             if (right != null && right.getValue() == value) return true;}

            // check down neighbor
            if (r + 1 < rows)
            {Tile down = grid[r + 1][c];
             if (down != null && down.getValue() == value) return true;}

        }
        return false; // no adjacent equal pair found so no valid move
    }

}
