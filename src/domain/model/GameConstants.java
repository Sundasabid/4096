package domain.model;

public final class GameConstants {
    private GameConstants() {}

    public static final int GRID_ROWS = 8;
    public static final int GRID_COLS = 5;
    public static final int MIN_MERGE_SIZE = 2;
    public static final int INITIAL_BEST_TILE = 2;

    public static final int UNDO_COST = 5;
    public static final int SHUFFLE_ALL_COST = 3;
    public static final int SHUFFLE_TWO_COST = 5;

    public static final int TILE_SIZE = 54;
    public static final int TILE_GAP = 5;

    public static final String STYLE_TILE_SELECTED = "tile-selected";

    public static final int ANIMATION_FADE_DURATION = 140;
    public static final int ANIMATION_POP_DURATION = 160;
    public static final double ANIMATION_SCALE_SELECTED = 0.92;
    public static final double ANIMATION_SCALE_POP = 1.12;

    public static final int DRAG_LINE_WIDTH = 8;
}