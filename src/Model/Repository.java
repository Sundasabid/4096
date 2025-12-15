package Model;

public class Repository {
    private Tile[][] prevGrid;
    private int prevBestTile;
    private final GameContext gameContext;
    public Repository(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public Tile[][] getPrevGrid() {
        return prevGrid;
    }

    public int getPrevBestTile() {
        return prevBestTile;
    }

    public void captureSnapshot(){
        prevBestTile=gameContext.player().getBestTile();
        prevGrid=gameContext.gameBoard().getGrid();
    }
}
