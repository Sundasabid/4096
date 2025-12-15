package app;

import Model.*;

public final class AppContext {
    public final SpawnLogic spawnLogic;
    public final PlayerSession playerSession;
    public final GameBoard gameBoard;
    public final GameContext gameContext;

    public final Repository repository;
    public final MergeLogic mergeLogic;
    public final MergeValidator mergeValidator;
    public final GameEnd gameEnd;

    public final ShuffleAllPowerUp shuffleAllPowerUp;
    public final ShuffleTwoPowerUp shuffleTwoPowerUp;
    public final UndoPowerUp undoPowerUp;

    public AppContext() {
        spawnLogic = new SpawnLogic();
        playerSession = new PlayerSession();
        gameBoard = new GameBoard(spawnLogic);
        gameContext = new GameContext(gameBoard, playerSession);

        shuffleAllPowerUp = new ShuffleAllPowerUp(gameContext);
        shuffleTwoPowerUp = new ShuffleTwoPowerUp(gameContext);

        repository = new Repository(gameContext);
        mergeLogic = new MergeLogic(gameContext, spawnLogic);
        mergeValidator = new MergeValidator(gameBoard);
        gameEnd = new GameEnd(gameBoard);

        undoPowerUp = new UndoPowerUp(gameContext, repository);
    }
}
