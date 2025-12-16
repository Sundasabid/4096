package domain.model;

import java.util.ArrayList;

public class MergeValidator {
    GameBoard gameBoard;
    public MergeValidator(GameBoard gameBoard) {this.gameBoard = gameBoard;}

    public boolean canAddCell(ArrayList<int[]> selectedCells, int newRow, int newCol) {
        Tile[][] grid = gameBoard.getGrid();

        try {if (grid[newRow][newCol] == null) return false;}
        catch (ArrayIndexOutOfBoundsException e) {return false;}

        if (selectedCells.isEmpty()) return true;

        // can't allow revisiting the same cell
        for (int[] pos : selectedCells)
            if (pos[0] == newRow && pos[1] == newCol) return false;

        int[] last = selectedCells.getLast();
        int r1 = last[0], c1 = last[1];

        try {
            Tile t1 = grid[r1][c1];
            Tile t2 = grid[newRow][newCol];
            if (!adjacentTile(t1, t2)) return false;

            int v1 = t1.getValue();
            int v2 = t2.getValue();

            int chainSize = selectedCells.size();

            if (chainSize == 1) return v2 == v1;
            else return ((v2 == v1) || (v2 == v1 * 2));

        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return false;
        }
    }
    private boolean adjacentTile(Tile a, Tile b) {
        int arow= a.getRow();
        int brow= b.getRow();
        int acol= a.getCol();
        int bcol= b.getCol();
        return (arow == brow || arow == brow + 1 || arow == brow - 1) && (acol == bcol || acol == bcol + 1 || acol == bcol - 1);
    }


}
