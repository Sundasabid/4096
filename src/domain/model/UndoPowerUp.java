package domain.model;

public class UndoPowerUp extends PowerUp{
    Repository repository;
    public UndoPowerUp(GameContext gameContext, Repository repository) {
        super(gameContext);
        this.cost=5;
        this.repository=repository;
    }

    public boolean activate(){
        if(canActivate())
        {   gameContext.gameBoard().setGrid(repository.getPrevGrid());
            gameContext.player().updateBestTile(repository.getPrevBestTile());
            useScore();
            return true;
        }return false;
    }
}
