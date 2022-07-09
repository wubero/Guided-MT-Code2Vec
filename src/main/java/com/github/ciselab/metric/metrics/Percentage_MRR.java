package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

import java.util.ArrayList;
import java.util.List;

/**
 * The percentage Mean Reciprocal Rank (Percentage_MRR) custom metric.
 */
public class Percentage_MRR extends Metric {

    public Percentage_MRR(String resultPath) {
        super("PercentageMRR", resultPath);
    }

    @Override
    public double calculateScore() {
        List<String> predictions = readPredictions(path);
        scores = new ArrayList<>();
        double score = 0;
        for(int i = 0; i < predictions.size(); i++) {
            String current = predictions.get(i);
            if(current.contains("No results for predicting:")) {
                score += 0;
                scores.add(0f);
            } else if(current.contains("score: ")) {
                double rank = Double.parseDouble(current.split("score: ")[1]);
                score += (rank/100);
                scores.add((float) (rank/100));
            }
        }
        return score/predictions.size();
    }
}
