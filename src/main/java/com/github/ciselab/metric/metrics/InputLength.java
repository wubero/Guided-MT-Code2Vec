package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import java.util.List;

public class InputLength extends Metric {

    private final String filePath = "C:\\Users\\Ruben-pc\\Documents\\Master_thesis\\Guided-MT-Code2Vec\\code2vec\\data\\";

    private static String dataset;

    public InputLength() {
        super("Input_length");
    }

    @Override
    public double CalculateScore() {
        if(dataset != null) {
            List<String> lines = readPredictions(filePath + dataset);
            return lines.size();
        }
        return 0;
    }

    public static void setDataSet(String dir) {
        dataset = dir;
    }
}
