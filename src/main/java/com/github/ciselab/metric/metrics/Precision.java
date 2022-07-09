package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

import java.util.ArrayList;
import java.util.List;

public class Precision extends Metric {

    public Precision(String resultPath) {
        super("Precision", resultPath);
    }

    @Override
    public double calculateScore() {
        scores = new ArrayList<>();
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("precision")) {
                score = Double.parseDouble(i.split("precision: ")[1].split(",")[0]);
            }
        }
        return score;
    }
}
