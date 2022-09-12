package com.github.ciselab.metric.metrics;

import com.github.ciselab.algorithms.MetamorphicIndividual;
import com.github.ciselab.metric.Metric;

import java.util.ArrayList;
import java.util.List;

public class Recall extends Metric {

    public Recall() {
        super("Recall");
    }

    public double calculateScore(String path) {
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("recall")) {
                score = Double.parseDouble(i.split("recall: ")[1].split(",")[0]);
            }
        }
        return score;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
    }
}
