package com.github.ciselab.support;

import com.github.ciselab.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.metric.Metric;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricCache {

    List<Metric> metricList; // Any at all, including secondary
    List<Metric> activeMetrics; // Metrics that Guide Fitness

    private final Map<MetamorphicIndividual, String> fileLookup = new HashMap<>();
    private final Map<MetamorphicIndividual, double[]> metricLookup = new HashMap<>();

    private final Map<MetamorphicIndividual,Map<Metric,Double>> lookup = new HashMap<>();

    private final Logger logger = LogManager.getLogger(MetricCache.class);

    public MetricCache() {
        metricList = new ArrayList<>();
        activeMetrics = new ArrayList<>();
    }

    public List<Metric> getMetrics() {
        return metricList;
    }

    public List<Metric> getSecondaryMetrics() {
        return metricList.stream().filter(x -> x.isSecondary()).toList();
    }

    public List<Double> getWeights() {
        return activeMetrics.stream().map(x -> x.getWeight()).toList();
    }

    public List<Boolean> getObjectives() {
        return activeMetrics.stream().map(x -> x.getObjective()).toList();
    }

    public void addMetric(Metric metric) {
        metricList.add(metric);
    }

    public void addSecondaryMetric(Metric metric) {
        secondaryMetrics.add(metric);
    }

    public void addMetricWeight(float weight) {
        metricWeights.add(weight);
    }

    public void setObjectives(boolean max) {
        for(int i = 0; i < metricWeights.size(); i++) {
            objectives[i] = max;
        }
    }

    public void setActiveMetrics(int activeMetrics) {
        this.activeMetrics = activeMetrics;
    }

    /**
     * Put a new transformer list and file combination into the fileLookup map.
     * @param transformers the list of transformers.
     * @param file the file name.
     */
    public void putFileCombination(MetamorphicIndividual transformers, String file) {
        fileLookup.put(transformers, file);
    }

    /**
     * Get the directory corresponding to the given genotype.
     * @param genotype the list of transformers.
     * @return the directory string if it exists, null otherwise.
     */
    public Optional<String> getDir(List<BaseTransformer> genotype) {
        String file = fileLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    /**
     * Create a key value pair of an individual and the corresponding fitness.
     * @param individual the individual.
     * @param fitness the fitness score.
     */
    public void fillFitness(MetamorphicIndividual individual, double[] fitness) {
        metricLookup.put(individual, fitness);
    }

    /**
     *Get the fitness score corresponding to a given transformers.
     * @param transformers the transformers.
     * @return the fitness score.
     */
    public Optional<double[]> getMetricResult(List<BaseTransformer> transformers) {
        double[] file = metricLookup.get(transformers);
        return Optional.ofNullable(file);
    }

    /**
     * Store the current genotype together with the fitness and filename in the map for later reference.
     * @param genotype the list of transformers.
     * @param fileName the file name.
     * @param score the fitness score.
     */
    public void storeFiles(MetamorphicIndividual genotype, String fileName, double[] score) {
        fileLookup.put(genotype, fileName);
        metricLookup.put(genotype, score);
    }

    /**
     * Initialize all weight properties and objectives.
     */
    public void initWeights(boolean max) {
        removeZeroWeights();
        normalizeWeights();
    }

    /**
     * Remove all metrics that have a weight of zero.
     * These do not have to be calculated or initialized.
     */
    private void removeZeroWeights() {
        var toRemove = activeMetrics.stream().filter(m -> m.getWeight() == 0).toList();
        activeMetrics.removeAll(toRemove);
    }

    /**
     * Normalizes the weights and ensures that there is at least one metric enabled.
     * The resulting weights will be summed up 1, with the old proportions kept.
     */
    private void normalizeWeights() {
        final double sum = activeMetrics.stream().mapToDouble(x -> x.getWeight()).sum();
        if (sum <= 0){
            logger.error("Combined weight is smaller or equal zero. There should be at least one metric enabled.");
            throw new IllegalArgumentException("There should be at least one metric enabled.");
        } else {
            activeMetrics.stream().forEach(
                    m -> m.setWeight(m.getWeight()/sum)
            );
        }
    }

    /**
     * Get the mean and standard deviation of each metric scores.
     * @return the pair of the metric means and standard deviation.
     */
    public Pair<double[], double[]> getStatistics() {
        double[] variance = new double[activeMetrics];
        double[] means = new double[activeMetrics];
        double[] sum = new double[activeMetrics];
        int size = metricLookup.values().size();

        for(int i = 0; i < activeMetrics; i++)
            sum[i] = 0;
        for(double[] value: metricLookup.values()) {
            for (int i = 0; i < activeMetrics; i++) {
                sum[i] += value[i];
            }
        }
        for(int i = 0; i < activeMetrics; i++)
            means[i] = sum[i]/size;

        for(int i = 0; i < activeMetrics; i++)
            sum[i] = 0;
        for(double[] value: metricLookup.values()) {
            for (int i = 0; i < activeMetrics; i++) {
                sum[i] += Math.pow((value[i] - means[i]), 2);
            }
        }
        for(int i = 0; i < activeMetrics; i++)
            variance[i] = Math.sqrt(sum[i]/size);
        return new ImmutablePair<>(means, variance);
    }
}
