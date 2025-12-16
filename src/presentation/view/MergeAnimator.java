package presentation.view;

import domain.model.GameConstants;
import domain.model.Position;
import javafx.animation.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;

public class MergeAnimator {

    public void playMergeAnimation(StackPane[][] tileNodes, List<Position> path, Runnable onComplete) {
        if (tileNodes == null || path.isEmpty()) {
            onComplete.run();
            return;
        }

        ParallelTransition disappear = new ParallelTransition();

        for (int i = 0; i < path.size() - 1; i++) {
            Position pos = path.get(i);
            StackPane node = tileNodes[pos.row()][pos.col()];
            if (node == null) continue;

            FadeTransition ft = new FadeTransition(
                    Duration.millis(GameConstants.ANIMATION_FADE_DURATION), node
            );
            ft.setToValue(0);

            ScaleTransition st = new ScaleTransition(
                    Duration.millis(GameConstants.ANIMATION_FADE_DURATION), node
            );
            st.setToX(0.6);
            st.setToY(0.6);

            disappear.getChildren().add(new ParallelTransition(ft, st));
        }

        Position lastPos = path.get(path.size() - 1);
        StackPane lastNode = tileNodes[lastPos.row()][lastPos.col()];

        ScaleTransition pop = new ScaleTransition(
                Duration.millis(GameConstants.ANIMATION_POP_DURATION), lastNode
        );
        pop.setToX(GameConstants.ANIMATION_SCALE_POP);
        pop.setToY(GameConstants.ANIMATION_SCALE_POP);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);

        SequentialTransition seq = new SequentialTransition(disappear, pop);
        seq.setOnFinished(e -> onComplete.run());
        seq.play();
    }
}