package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.io.File;
import java.util.List;

public class Recall extends Metric {
    private static final String EXPECTEDFILE =  "F1_score_log.txt";

    public Recall() {
        this.name = Name.REC;
    }

    public double calculateScore(String path) {
        if(!path.contains("results"))
            path = path + File.separator + "results";
        List<String> lines = readPredictions(path + File.separator + EXPECTEDFILE);
        Double score = Double.NaN;
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
