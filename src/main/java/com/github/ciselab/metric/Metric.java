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
public abstract class Metric implements Comparable<Metric> {

    protected double score;
    protected String name;
    protected String path;
    protected String objective;
    protected final Logger logger = LogManager.getLogger(Metric.class);

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
        if(new File(resultPath).isDirectory())
            path = resultPath;
        else
            logger.error("Path does not exist: " + resultPath);
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
     * @param filePath the file path.
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
            logger.debug("Couldn't read file of path: " + filePath);
        }
        return predictions;
    }

    /**
     * The calculateScore function calculates the score for this metric according to the result files.
     * @return the score for this metric.
     */
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
