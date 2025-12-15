package Model;

public class PlayerSession {
    private int score;
    private int diamonds;
    private int bestTile;

//    use cases:
//    1. updateScore (add) when use makes new moves
//    2. increase diamonds when user makes moves.
    public void addScore(int score){this.score+=score;
    diamonds=score/10;
    }

//    1. getDiamonds when need to see if powerups are allowed.
//    2. getDiamonds to show on the UI.
    public int getDiamonds() {
    return score/10;
    }

//    1. decrease Diamonds when activate Powerups.
//    2. decrease score when diamonds are used.
    public void decreaseDiamonds(int d){
    diamonds-=d;
    score=score-d*10;
    }

    public void updateBestTile(int bestTile){this.bestTile=bestTile;}
    public int getBestTile() {
        if(bestTile<2)return 2;
        return bestTile;}

}