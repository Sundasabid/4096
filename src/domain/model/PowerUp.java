package domain.model;

public class PowerUp {
    protected int cost;
    protected final GameContext gameContext;

    public PowerUp(GameContext g) {
        this.gameContext = g;
    }
    public boolean canActivate(){
        return gameContext.player().getDiamonds() >= cost;
    }
    public void useScore(){
        gameContext.player().decreaseDiamonds(cost);
    }

}
