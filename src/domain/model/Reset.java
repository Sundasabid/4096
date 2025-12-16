package domain.model;

public class Reset {
    public void resetGame(GameContext g){
        GameContext gameContext=g;
        gameContext.player().decreaseDiamonds(gameContext.player().getDiamonds());
        gameContext.player().updateBestTile(2);
        gameContext.gameBoard().newGrid();
    }
}
