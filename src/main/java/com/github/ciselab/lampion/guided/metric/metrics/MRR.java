package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The Mean Reciprocal Rank (MRR) metric.
 */
public class MRR extends Metric {

    private static final String EXPECTEDFILE =  "results.txt";
    public MRR() {
        this.name = Name.MRR;
    }

    private double calculateScore(String path) {
        if(!path.contains("results"))
            path = path + File.separator + "results";
        List<String> predictions = readPredictions(path  + File.separator + EXPECTEDFILE);
        var scores = new ArrayList<>();
        float score = 0;
        for(int i = 0; i < predictions.size(); i++) {
            String current = predictions.get(i);
            if(current.contains("No results for predicting:")) {
                score += 0;
                scores.add(0f);
            } else if(current.contains("predicted correctly at rank: ")) {
                double rank = Integer.parseInt(current.split("rank: ")[1].split(",")[0]);
                score += (1/rank);
                scores.add((float) (1/rank));
            } else {
                score += 1;
                scores.add(1f);
            }
        }
        return score/predictions.size();
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        double score =  individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
        if(!objective)
            return 1-score;
        else
            return score;
    }
}
