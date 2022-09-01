package com.github.ciselab.metric;

import com.github.ciselab.support.GenotypeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The abstract class for a metric.
 */
public abstract class Metric {

    protected double score; // The score for this metric
    protected String name; // The name of the metric
    protected String path; // The path from which it should read its results
    protected String objective; // Whether it should minimize or maximize the metric
    protected final Logger logger = LogManager.getLogger(Metric.class); // The logger for this class
    protected List<Float> scores;

    public Metric(String metricName) {
        name = metricName;
        score = -1;
        path = GenotypeSupport.dir_path + "/code2vec/results.txt";
    }

    /**
     * Extra constructor so we can set a different file path for the results.
     * @param metricName the name of the metric.
     * @param resultPath the result path.
     */
    public Metric(String metricName, String resultPath) {
        name = metricName;
        score = -1;
        path = resultPath;
    }

    public List<Float> getScores() {
        return scores;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    /**
     * Read predictions from a particular file path.
     * @param filePath the file path, should include the code2vec model's results.
     * @return a list of strings with the predictions.
     */
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
            logger.error("Couldn't read file of path: " + filePath);
        }
        return predictions;
    }

    /**
     * The calculateScore function calculates the score for this metric according to the result files.
     * @return the score for this metric.
     */
    public abstract double calculateScore();
}
