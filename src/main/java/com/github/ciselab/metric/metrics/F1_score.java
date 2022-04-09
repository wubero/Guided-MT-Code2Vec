package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

/**
 * The F1_Score metric.
 * This metric is already calculated by the code2vec project and gotten from a file.
 */
public class F1_score extends Metric {

    private final String filePath = "C:\\Users\\Ruben-pc\\Documents\\Master_thesis\\Guided-MT-Code2Vec\\code2vec\\F1_score_log.txt";

    public F1_score() {
        super("F1_score");
    }

    @Override
    public double CalculateScore() {
        List<String> lines = readPredictions(filePath);
        double score = -1;
        for(String i: lines) {
            if(i.contains("F1")) {
                score = Double.parseDouble(i.split("F1: ")[1]);
            }
        }
        return score;
    }
}
