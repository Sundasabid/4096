package domain.model;

import domain.model.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnLogic {

    // Random no generator
    private final Random rng = new Random();

    // Creates a new tile at given row and column
    // tile value is decided by getSpawnValue() method
    public Tile spawnTile(int row, int col, int bestTile) {
        return new Tile(getSpawnValue(bestTile), row, col);
    }

    // =========================
    // NEW SPAWN ALGORITHM
    // =========================

    private static final class Stage {
        final long minTile;      // inclusive
        final long maxTile;      // exclusive (unless INF stage)
        final boolean infMax;
        final int minSpawn;
        final int maxSpawn;

        Stage(long minTile, long maxTile, boolean infMax, int minSpawn, int maxSpawn) {
            this.minTile = minTile;
            this.maxTile = maxTile;
            this.infMax = infMax;
            this.minSpawn = minSpawn;
            this.maxSpawn = maxSpawn;
        }

        boolean contains(long bestTile) {
            if (infMax) return bestTile >= minTile;
            return bestTile >= minTile && bestTile < maxTile; // half-open interval
        }
    }

    private static final Stage[] STAGES = {
            new Stage(0L,      2048L,   false, 2,   64),
            new Stage(2048L,   16384L,  false, 4,  128),
            new Stage(16384L,  32768L,  false, 8,  128),
            new Stage(32768L,  65536L,  false, 16, 256),
            new Stage(65536L,  131072L, false, 32, 512),
            new Stage(131072L, 0L,      true,  64, 1024) // INF stage
    };

    private int getSpawnValue(long bestTile) {
        Stage stage = getSpawnRange(bestTile);
        int sub = getSubThreshold(bestTile, stage);

        List<Integer> values = getPossibleValues(stage.minSpawn, stage.maxSpawn);
        double[] probs = getProbabilities(values.size(), sub);

        return weightedRandomChoice(values, probs);
    }

    private Stage getSpawnRange(long bestTile) {
        for (Stage s : STAGES) {
            if (s.contains(bestTile)) return s;
        }
        return STAGES[STAGES.length - 1];
    }

    private int getSubThreshold(long bestTile, Stage stage) {
        long rangeStart = stage.minTile;

        // INF stage: use finite virtual end only for threshold splitting
        long rangeEndForThreshold = stage.infMax ? (rangeStart * 2) : stage.maxTile;

        long span = rangeEndForThreshold - rangeStart;
        double t1 = rangeStart + (span / 3.0);
        double t2 = rangeStart + (span * 2.0 / 3.0);

        if (bestTile < t1) return 1;
        if (bestTile < t2) return 2;
        return 3;
    }

    private List<Integer> getPossibleValues(int min, int max) {
        List<Integer> values = new ArrayList<>();
        int current = min;
        while (current <= max) {
            values.add(current);
            current *= 2;
        }
        return values;
    }

    private double[] getProbabilities(int n, int subThreshold) {
        double[] weights = new double[n];
        double ratio = 0.70; // 0.65 = more small-heavy, 0.75 = more balanced
        for (int i = 0; i < n; i++) {
            weights[i] = Math.pow(ratio, i);
        }

//        if (subThreshold == 1) {
//            // Exponential decay: 1, 1/2, 1/4, ...
//            for (int i = 0; i < n; i++) {
//                weights[i] = 1.0 / (1L << i); // 2^i
//            }
//        } else
        if (subThreshold == 2) {
            // Slower decay (more balanced)
            for (int i = 0; i < n; i++) {
                weights[i] = 1.0 / (1.0 + i * 0.5);
            }
        } else {
            // Big half heavy: 60% top half, 40% bottom half
            int mid = n / 2; // floor
            for (int i = 0; i < n; i++) {
                if (i < mid) {
                    weights[i] = 0.40 / mid;
                } else {
                    weights[i] = 0.60 / (n - mid);
                }
            }
        }

        // Normalize
        double total = 0.0;
        for (double w : weights) total += w;

        double[] probs = new double[n];
        for (int i = 0; i < n; i++) probs[i] = weights[i] / total;

        return probs;
    }

    private int weightedRandomChoice(List<Integer> values, double[] probs) {
        double r = rng.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < values.size(); i++) {
            cumulative += probs[i];
            if (r <= cumulative) return values.get(i);
        }
        return values.get(values.size() - 1); // fallback
    }
}