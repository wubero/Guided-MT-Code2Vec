package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

/**
 * The F1_Score metric.
 * This metric is already calculated by the code2vec project and gotten from a file.
 */
public class F1_score extends Metric {

    public F1_score(String resultPath) {
        super("F1Score", resultPath);
    }

    @Override
    public double calculateScore() {
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("F1")) {
                score = Double.parseDouble(i.split("F1: ")[1]);
            }
        }
        return score;
    }
}
