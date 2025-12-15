package Model;

public class Tile {
    private int value;
    private int row, col;

    public Tile(int value, int row, int col) {
        this.value = value;
        this.row=row;
        this.col=col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setIndex(int r, int c){
        row=r; col=c;
    }
    public int getValue() {
        return value;
    }
    public Tile copy() {
        return new Tile(this.value, this.row, this.col);
    }

}
