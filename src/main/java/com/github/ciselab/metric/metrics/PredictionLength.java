package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import com.github.ciselab.support.GenotypeSupport;
import java.util.List;

public class PredictionLength extends Metric {

    private final String filePath = GenotypeSupport.dir_path + "/code2vec/predicted_words.txt";

    public PredictionLength() {
        super("PredictionLength");
    }

    @Override
    public double calculateScore() {
        // Original: render, predicted: get|logs
        List<String> lines = readPredictions(filePath);
        double score = 0;
        for(String i: lines) {
            if(i.contains("predicted")) {
                String predicted = i.split(": ")[2];
                for(int j = 0; j < predicted.length(); j++) {
                    if(predicted.charAt(j) != '|')
                        score++;
                }
            }
        }
        return score/lines.size();
    }
}
