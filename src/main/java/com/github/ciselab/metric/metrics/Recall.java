package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

public class Recall extends Metric {

    private final String filePath = "C:\\Users\\Ruben-pc\\Documents\\Master_thesis\\Guided-MT-Code2Vec\\code2vec\\F1_score_log.txt";

    public Recall() {
        super("Recall");
    }

    @Override
    public double CalculateScore() {
        List<String> lines = readPredictions(filePath);
        double score = -1;
        for(String i: lines) {
            if(i.contains("recall")) {
                score = Double.parseDouble(i.split("recall: ")[1].split(",")[0]);
            }
        }
        return score;
    }
}
