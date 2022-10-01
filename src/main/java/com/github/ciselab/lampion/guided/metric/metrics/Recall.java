package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        double score =  individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
        if(!objective)
            return 1-score;
        else
            return score;
    }

    @Override
    public boolean canBeBiggerThanOne() {
        return false;
    }


    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (o instanceof Recall ed) {
            return ed.getWeight() == this.getWeight();
        }
        return false;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(name).append(weight).hashCode();
    }

}
