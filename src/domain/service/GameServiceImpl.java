package domain.service;

import domain.model.Position;
import domain.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl implements GameService {
    private final GameContext gameContext;
    private final MergeValidator validator;
    private final MergeLogic mergeLogic;
    private final GameEnd gameEnd;
    private final Repository repository;
    private final ShuffleAllPowerUp shuffleAllPowerUp;
    private final ShuffleTwoPowerUp shuffleTwoPowerUp;
    private final UndoPowerUp undoPowerUp;

    public GameServiceImpl(GameContext context, MergeValidator validator,
                           MergeLogic mergeLogic, GameEnd gameEnd, Repository repository,
                           ShuffleAllPowerUp shuffleAll, ShuffleTwoPowerUp shuffleTwo,
                           UndoPowerUp undo) {
        this.gameContext = context;
        this.validator = validator;
        this.mergeLogic = mergeLogic;
        this.gameEnd = gameEnd;
        this.repository = repository;
        this.shuffleAllPowerUp = shuffleAll;
        this.shuffleTwoPowerUp = shuffleTwo;
        this.undoPowerUp = undo;
    }

    @Override
    public boolean canMerge(List<Position> positions) {
        if (positions.size() < 2) return false;

        ArrayList<int[]> legacyPositions = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            Position current = positions.get(i);
            legacyPositions.add(current.toArray());

            if (i > 0) {
                if (!validator.canAddCell((ArrayList<int[]>) legacyPositions.subList(0, i), current.row(), current.col())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void executeMerge(List<Position> positions) {
        repository.captureSnapshot();
        ArrayList<int[]> legacy = new ArrayList<>();
        positions.forEach(p -> legacy.add(p.toArray()));
        mergeLogic.activate(legacy);
    }

    @Override
    public boolean isGameOver() {
        return gameEnd.activate();
    }

    @Override
    public void resetGame() {
        new Reset().resetGame(gameContext);
    }

    @Override
    public void captureSnapshot() {
        repository.captureSnapshot();
    }

    @Override
    public boolean undoLastMove() {
        return undoPowerUp.activate();
    }

    @Override
    public boolean shuffleAll() {
        return shuffleAllPowerUp.activate();
    }

    @Override
    public void shuffleTwo(Position pos1, Position pos2) {
        int[][] positions = new int[][]{pos1.toArray(), pos2.toArray()};
        shuffleTwoPowerUp.activate(positions);
    }

    @Override
    public int getDiamonds() {
        return gameContext.player().getDiamonds();
    }

    @Override
    public int getBestTile() {
        return gameContext.player().getBestTile();
    }
}