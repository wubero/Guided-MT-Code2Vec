package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InputLength extends Metric {

    private String dataset;

    public InputLength(){
        this.name = Name.INPUTLENGTH;
    }

    @Override
    public boolean isSecondary() {
        return true;
    }

    private double calculateScore(String path) {
        var scores = new ArrayList<>();
        if(dataset != null) {
            // should read all files not the dataset...
            int count = 0;
            try {
                File file = new File(path + dataset + "/test");
                for (File i : file.listFiles()) {
                    List<String> lines = readPredictions(i.getPath());
                    count += lines.size();
                    scores.add((float) lines.size());
                }
                return count;
            } catch(NullPointerException e) {
                logger.debug("Couldn't get files input length set to 0.");
            }
        }
        return 0;
    }

    public void setDataSet(String dir) {
        dataset = dir;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return individual.getResultPath()
                .map(i -> calculateScore(i))
                .orElse(0.0);
    }
}
