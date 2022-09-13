package com.github.ciselab.lampion.guided.metric;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The abstract class for a metric.
 */
public abstract class Metric implements Function<MetamorphicIndividual, Double> {
    public enum Name {
        MRR,
        F1,
        PMRR,
        PREC,
        REC,
        EDITDIST,
        PREDLENGTH,
        /**
         * Secondary metrics are not required to use the
         */
        INPUTLENGTH,
        TRANSFORMATIONS,
        // Error Case - for warnings
        UNIMPLEMENTED
    }

    protected Name name = Name.UNIMPLEMENTED;

    protected boolean objective = false; // Whether it should minimize or maximize the metric, true = maximize, false = minimize
    protected double weight = 0; // How much the Metric is weighted for mixed weight calculations
    protected final Logger logger = LogManager.getLogger(Metric.class); // The logger for this class

    public void setObjective(String obj) {
        objective = obj.equalsIgnoreCase("max");
    }

    public void setObjective(boolean obj){
        objective = obj;
    }

    public boolean getObjective(){
        return objective;
    }

    public String getName() {
        return name.toString();
    }

    public double getWeight(){return weight;}
    public void setWeight(double weight){
        if (weight < 0){
            throw new UnsupportedOperationException("Received negative weight for Metric - weight must be 0 or more");
        }
        if (weight > 1){
            logger.warn("Unusual weight received for Metric: " + weight);
        }
        this.weight = weight;
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
    @Override
    public boolean equals(Object o) {
        if (o == this){
            return true;
        }
        if (o instanceof Metric){
            var m = (Metric) o;
            return m.getName().equals(this.getName());
        }
        return false;
    }

    public abstract boolean isSecondary();

    /**
     * Maps a given String to the known Metric Enum Type
     * @param str the string that might match one of the Implemented Metrics
     * @return the resolved Enum Constant, UNIMPLEMENTED in case of failure.
     */
    public static final Name resolveName(String str){
        return switch(str.toLowerCase()) {
            case "mrr" -> Name.MRR;
            case "pmrr" -> Name.PMRR;
            case "percentagemrr" -> Name.PMRR;
            case "f1" -> Name.F1;
            case "f1score" -> Name.F1;
            case "f1_score" -> Name.F1;
            case "prec" -> Name.PREC;
            case "precision" -> Name.PREC;
            case "rec" -> Name.REC;
            case "recall" -> Name.REC;
            case "edit" -> Name.EDITDIST;
            case "editdistance" -> Name.EDITDIST;
            case "pred" -> Name.PREDLENGTH;
            case "prediction" -> Name.PREDLENGTH;
            case "predictionlength" -> Name.PREDLENGTH;
            case "input" -> Name.INPUTLENGTH;
            case "inputlength" -> Name.INPUTLENGTH;
            case "trans" -> Name.TRANSFORMATIONS;
            case "transformations" -> Name.TRANSFORMATIONS;
            case "numeroftransformations" -> Name.TRANSFORMATIONS;
            default -> Name.UNIMPLEMENTED;
        };

    }
}
