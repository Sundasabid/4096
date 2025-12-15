package Model;

public class ShuffleTwoPowerUp extends PowerUp{


    public ShuffleTwoPowerUp(GameContext g) {
        super(g);this.cost=5;
    }

    public void activate(int[][] array) {

        GameBoard gameBoard = gameContext.gameBoard();

        Tile[][] copy = gameBoard.getGrid();

        int r1 = array[0][0];
        int c1 = array[0][1];
        int r2 = array[1][0];
        int c2 = array[1][1];

        int v1 = copy[r1][c1].getValue();
        int v2 = copy[r2][c2].getValue();

        // write back to real board (swap values)
        gameBoard.updateTileValue(r1, c1, v2);
        gameBoard.updateTileValue(r2, c2, v1);

        useScore();
    }


}
