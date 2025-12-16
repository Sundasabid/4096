package domain.model;

import domain.model.SpawnLogic;
import domain.model.Tile;

public class GameBoard {
    private final int row=8,col=5;
    private Tile[][] grid=new Tile[row][col];
    SpawnLogic spawnLogic;
    //All possible tile values
    private static final int[] ALL_TILE_VALUES = {
//            update to make automatic
            2, 4, 8, 16, 32, 64, 128, 256,
            512, 1024, 2048, 4096, 8192, 16384,
            32768, 65536, 131072, 262144, 524288,
            1048576
    };

    //Fill each cell in grid with new tiles usin spawn logic
    public GameBoard(SpawnLogic spawnLogic) {
        this.spawnLogic=spawnLogic;
        for(int i=0;i<row;i++) for(int j=0;j<col;j++)
//                i kept best tile 2 because gameboard constructor iscalled at the very start of a game session,
//                best tile is 2 till then.
            grid[i][j]= spawnLogic.spawnTile(i,j,2);
    }
    public void newGrid(){
        for(int i=0;i<row;i++) for(int j=0;j<col;j++)
            grid[i][j]= spawnLogic.spawnTile(i,j,2);
    }
    public int[] getALL_TILE_VALUES() {
        return ALL_TILE_VALUES;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }
    public void setTile(Tile tile, int row, int cols){
        grid[row][cols]=tile;
    }
    public void updateTileValue(int row, int col, int value){
        grid[row][col].setValue(value);
    }

    public Tile[][] getGrid() {
        Tile[][] copy = new Tile[row][col];
        for (int i = 0; i < row; i++) for (int j = 0; j < col; j++)
            if (this.grid[i][j] != null) copy[i][j] = this.grid[i][j].copy();
            else copy[i][j] = null;

        return copy;
    }
}