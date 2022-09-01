package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

import java.util.ArrayList;
import java.util.List;

public class PredictionLength extends Metric {

    public PredictionLength(String resultPath) {
        super("PredictionLength", resultPath);
    }

    @Override
    public double calculateScore() {
        // Original: render, predicted: get|logs
        List<String> lines = readPredictions(path);
        scores = new ArrayList<>();
        double score = 0;
        for(String i: lines) {
            if(i.contains("predicted") && i.contains("Original")) {
                String predicted = i.split(": ")[2];
                for(int j = 0; j < predicted.length(); j++) {
                    int count = 0;
                    if(predicted.charAt(j) != '|')
                        count++;
                    score += count;
                    scores.add((float) count);
                }
            }
        }
        return score/lines.size();
    }
}
