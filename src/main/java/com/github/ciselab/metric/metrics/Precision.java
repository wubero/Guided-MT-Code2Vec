package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import com.github.ciselab.support.GenotypeSupport;
import java.util.List;

public class Precision extends Metric {

    private final String filePath = GenotypeSupport.dir_path + "/code2vec/F1_score_log.txt";

    public Precision() {
        super("Precision");
    }

    @Override
    public double CalculateScore() {
        List<String> lines = readPredictions(filePath);
        double score = -1;
        for(String i: lines) {
            if(i.contains("precision")) {
                score = Double.parseDouble(i.split("precision: ")[1].split(",")[0]);
            }
        }
        return score;
    }
}
