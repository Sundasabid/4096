package presentation.view;
import domain.model.GameConstants;
import domain.model.Tile;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TileRenderer {

    public StackPane[][] renderGrid(GridPane gameGrid, Tile[][] grid) {
        gameGrid.getChildren().clear();
        StackPane[][] tileNodes = new StackPane[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                StackPane tile = createTileNode(grid[row][col]);
                tileNodes[row][col] = tile;
                gameGrid.add(tile, col, row);
            }
        }

        return tileNodes;
    }

    private StackPane createTileNode(Tile tile) {
        int value = tile.getValue();

        StackPane node = new StackPane();
        node.setPickOnBounds(true);

        node.setMinSize(GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        node.setPrefSize(GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        node.setMaxSize(GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

        node.getStyleClass().add(value == 0 ? "tile-0" : "tile-" + value);

        Label label = new Label(value == 0 ? "" : String.valueOf(value));
        label.setFont(new Font(22));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold;");
        label.setMouseTransparent(true);

        node.getChildren().add(label);

        return node;
    }

    public void highlightTile(StackPane node) {
        if (node != null && !node.getStyleClass().contains(GameConstants.STYLE_TILE_SELECTED)) {
            node.getStyleClass().add(GameConstants.STYLE_TILE_SELECTED);
        }
    }

    public void clearHighlights(StackPane[][] tileNodes) {
        if (tileNodes == null) return;
        for (StackPane[] row : tileNodes) {
            for (StackPane node : row) {
                if (node != null) {
                    node.getStyleClass().remove(GameConstants.STYLE_TILE_SELECTED);
                }
            }
        }
    }
}