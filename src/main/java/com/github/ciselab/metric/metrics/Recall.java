package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

public class Recall extends Metric {

    public Recall(String resultPath) {
        super("Recall", resultPath);
    }

    @Override
    public double calculateScore() {
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("recall")) {
                score = Double.parseDouble(i.split("recall: ")[1].split(",")[0]);
            }
        }
        return score;
    }
}
