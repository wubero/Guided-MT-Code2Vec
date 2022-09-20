package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PredictionLength extends Metric {

    private static final String EXPECTEDFILE = "predicted_words.txt";
    public PredictionLength() {
        this.name = Name.PREDLENGTH;
    }

    @Override
    public boolean isSecondary() {
        return true;
    }

    private double calculateScore(String path) {
        if(!path.contains("results"))
            path = path + File.separator + "results";
        // Original: render, predicted: get|logs
        List<String> lines = readPredictions(path  + File.separator + EXPECTEDFILE);
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
        return  individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
    }

    @Override
    public boolean canBeBiggerThanOne() {
        return true;
    }
}
