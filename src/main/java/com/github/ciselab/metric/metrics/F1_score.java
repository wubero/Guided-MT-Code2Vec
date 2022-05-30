package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import com.github.ciselab.support.GenotypeSupport;
import java.util.List;

/**
 * The F1_Score metric.
 * This metric is already calculated by the code2vec project and gotten from a file.
 */
public class F1_score extends Metric {

    private final String filePath = GenotypeSupport.dir_path + "/code2vec/F1_score_log.txt";

    public F1_score() {
        super("F1Score");
    }

    @Override
    public double calculateScore() {
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
