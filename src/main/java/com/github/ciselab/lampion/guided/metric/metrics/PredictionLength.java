package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.util.ArrayList;
import java.util.List;

public class PredictionLength extends Metric {

    public PredictionLength() {
        this.name = Name.PREDLENGTH;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    private double calculateScore(String path) {
        // Original: render, predicted: get|logs
        List<String> lines = readPredictions(path);
        var scores = new ArrayList<>();
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

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
    }
}
