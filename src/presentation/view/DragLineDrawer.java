package presentation.view;

import domain.model.GameConstants;
import domain.model.Position;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class DragLineDrawer {
    private final Pane overlayPane;
    private final GridPane gameGrid;
    private final StackPane[][] tileNodes;
    private Polyline dragLine;

    public DragLineDrawer(Pane overlayPane, GridPane gameGrid, StackPane[][] tileNodes) {
        this.overlayPane = overlayPane;
        this.gameGrid = gameGrid;
        this.tileNodes = tileNodes;
    }

    public void startLine(Position position) {
        overlayPane.getChildren().clear();

        dragLine = new Polyline();
        dragLine.setMouseTransparent(true);
        dragLine.setStroke(Color.WHITE);
        dragLine.setStrokeWidth(GameConstants.DRAG_LINE_WIDTH);
        dragLine.setStrokeLineCap(StrokeLineCap.ROUND);
        dragLine.setStrokeLineJoin(StrokeLineJoin.ROUND);

        Point2D point = getCellCenter(position);
        dragLine.getPoints().addAll(point.getX(), point.getY());
        overlayPane.getChildren().add(dragLine);
    }

    public void extendLine(Position position) {
        if (dragLine == null) return;
        Point2D point = getCellCenter(position);
        dragLine.getPoints().addAll(point.getX(), point.getY());
    }

    public void clearLine() {
        if (overlayPane != null) {
            overlayPane.getChildren().clear();
        }
        dragLine = null;
    }

    private Point2D getCellCenter(Position position) {
        if (overlayPane == null || gameGrid == null || tileNodes == null) {
            return new Point2D(0, 0);
        }

        StackPane tile = tileNodes[position.row()][position.col()];
        if (tile == null) return new Point2D(0, 0);

        double w = tile.getLayoutBounds().getWidth();
        double h = tile.getLayoutBounds().getHeight();

        double xInGrid = tile.getLayoutX() + w / 2.0;
        double yInGrid = tile.getLayoutY() + h / 2.0;

        Point2D scenePoint = gameGrid.localToScene(xInGrid, yInGrid);
        return overlayPane.sceneToLocal(scenePoint);
    }
}