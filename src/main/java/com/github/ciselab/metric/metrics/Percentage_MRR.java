package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

public class Percentage_MRR extends Metric {

    public Percentage_MRR() {
        super("Percentage_MRR");
    }

    @Override
    public double CalculateScore() {
        List<String> predictions = readPredictions(defaultPath);
        double score = 0;
        for(int i = 0; i < predictions.size(); i++) {
            String current = predictions.get(i);
            if(current.contains("No results for predicting:")) {
                score += 0;
            } else if(current.contains("score: ")) {
                double rank = Double.parseDouble(current.split("score: ")[1]);
                score += (rank/100);
            }
        }
        return score/predictions.size();
    }
}