package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

public class F1_score extends Metric {

    public F1_score() {
        super("F1_score");
    }

    @Override
    public double CalculateScore() {
        String filePath = "C:\\Users\\Ruben-pc\\Documents\\Master_thesis\\Guided-MT-Code2Vec\\code2vec\\F1_score_log.txt";
        List<String> lines = readPredictions(filePath);
        double score = -1;
        for(String i: lines) {
            if(i.contains("F1")) {
                score = Double.parseDouble(i.split("F1: ")[1]);
            }
        }
        return score;
    }
}
