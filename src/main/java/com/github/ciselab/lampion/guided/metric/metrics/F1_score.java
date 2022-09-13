package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.util.List;

/**
 * The F1_Score metric.
 * This metric is already calculated by the code2vec project and gotten from a file.
 */
public class F1_score extends Metric {

    public F1_score() {
        super("F1Score");
    }

    private double calculateScore(String path) {
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("F1")) {
                score = Double.parseDouble(i.split("F1: ")[1]);
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
