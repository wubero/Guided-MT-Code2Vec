package com.github.ciselab.metric;

import com.github.ciselab.support.GenotypeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Metric implements Comparable<Metric> {

    protected double score;
    protected String name;
    protected final String defaultPath = GenotypeSupport.dir_path + "/code2vec/results.txt";
    protected String objective;
    protected final Logger logger = LogManager.getLogger(Metric.class);

    public Metric(String metricName) {
        this.name = metricName;
        this.score = -1;
    }

    public void setObjective(String obj) {
        objective = obj;
    }

    public String getObjective(){
        return objective;
    }

    public String getName() {
        return name;
    }

    public List<String> readPredictions(String filePath) {
        List<String> predictions = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            String line = bf.readLine();
            while(line != null) {
                predictions.add(line);
                line = bf.readLine();
            }
            bf.close();
        } catch(IOException e) {
            logger.debug("Couldn't read file of path: " + filePath);
        }
        return predictions;
    }

    public abstract double calculateScore();

    @Override
    public int compareTo(Metric other) {
        if(this.score > other.score)
            return 0;
        else if(score < other.score)
            return 1;
        else
            return -1;
    }
}
