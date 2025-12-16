package presentation.view;

import domain.model.Position;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public class InputHandler {
    private Consumer<Position> onTilePressed;
    private Consumer<Position> onTileDragged;
    private Runnable onDragFinished;

    public void setOnTilePressed(Consumer<Position> handler) {
        this.onTilePressed = handler;
    }

    public void setOnTileDragged(Consumer<Position> handler) {
        this.onTileDragged = handler;
    }

    public void setOnDragFinished(Runnable handler) {
        this.onDragFinished = handler;
    }

    public void attachMergeHandlers(StackPane tile, Position position) {
        tile.setOnMousePressed(e -> {
            if (onTilePressed != null) {
                onTilePressed.accept(position);
            }
            e.consume();
        });

        tile.setOnDragDetected(e -> {
            tile.startFullDrag();
            e.consume();
        });

        tile.setOnMouseDragEntered(e -> {
            if (onTileDragged != null) {
                onTileDragged.accept(position);
            }
            e.consume();
        });

        tile.setOnMouseReleased(e -> {
            if (onDragFinished != null) {
                onDragFinished.run();
            }
            e.consume();
        });
    }

    public void attachClickHandler(StackPane tile, Position position, Consumer<Position> onClick) {
        tile.setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.accept(position);
            }
            e.consume();
        });
    }
}