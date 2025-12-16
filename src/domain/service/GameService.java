package domain.service;

import domain.model.Position;
import java.util.List;

public interface GameService {
    boolean canMerge(List<Position> positions);
    void executeMerge(List<Position> positions);
    boolean isGameOver();
    void resetGame();
    void captureSnapshot();
    boolean undoLastMove();
    boolean shuffleAll();
    void shuffleTwo(Position pos1, Position pos2);
    int getDiamonds();
    int getBestTile();
}