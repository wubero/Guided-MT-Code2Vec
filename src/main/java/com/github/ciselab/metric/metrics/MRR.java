package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

/**
 * The Mean Reciprocal Rank (MRR) metric.
 */
public class MRR extends Metric {

    public MRR() {
        super("MRR");
    }

    @Override
    public double calculateScore() {
        List<String> predictions = readPredictions(defaultPath);
        float score = 0;
        for(int i = 0; i < predictions.size(); i++) {
            String current = predictions.get(i);
            if(current.contains("No results for predicting:")) {
                score += 0;
            } else if(current.contains("predicted correctly at rank: ")) {
                int rank = Integer.parseInt(current.split("rank: ")[1].split(",")[0]);
                score += (1/rank);
            } else {
                score += 1;
            }
        }
        return score/predictions.size();
    }
}
