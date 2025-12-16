package domain.model;

public record Position(int row, int col) {
    public boolean isAdjacent(Position other) {
        int rowDiff = Math.abs(row - other.row);
        int colDiff = Math.abs(col - other.col);
        return rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0);
    }

    public int[] toArray() {
        return new int[]{row, col};
    }

    public static Position from(int[] array) {
        return new Position(array[0], array[1]);
    }
}