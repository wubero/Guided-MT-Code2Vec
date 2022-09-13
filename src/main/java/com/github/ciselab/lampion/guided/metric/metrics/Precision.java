package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.util.List;

public class Precision extends Metric {

    public Precision() {
        this.name = Name.PREC;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    private double calculateScore(String path) {
        List<String> lines = readPredictions(path);
        double score = -1;
        for(String i: lines) {
            if(i.contains("precision")) {
                score = Double.parseDouble(i.split("precision: ")[1].split(",")[0]);
            }
        }
        return score;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
    }
}
