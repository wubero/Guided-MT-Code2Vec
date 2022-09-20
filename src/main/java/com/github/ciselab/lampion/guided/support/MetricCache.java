package com.github.ciselab.lampion.guided.support;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.guided.metric.Metric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricCache {

    List<Metric> metricList = new ArrayList<>(); // Any at all, including secondary
    List<Metric> activeMetrics = new ArrayList<>(); // Metrics that Guide Fitness

    private final Map<MetamorphicIndividual, String> fileLookup = new HashMap<>();

    private final Map<MetamorphicIndividual,Map<Metric,Double>> lookup = new HashMap<>();

    private final Logger logger = LogManager.getLogger(MetricCache.class);

    public List<Metric> getMetrics() {
        return metricList;
    }

    public List<Metric> getActiveMetrics() { return activeMetrics;}

    public List<Double> getWeights() {
        return activeMetrics.stream().map(x -> x.getWeight()).toList();
    }

    public void addMetric(Metric metric) {
        metricList.add(metric);
        if (metric.getWeight()>0){
            activeMetrics.add(metric);
        }
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
     * @param fitnesses the fitness score.
     */
    public void fillFitness(MetamorphicIndividual individual, Map<Metric,Double> fitnesses) {
        lookup.put(individual, fitnesses);
    }

    /**
     *Get the fitness score corresponding to a given transformers.
     * @param individual the metamorphic individual.
     * @return the fitness scores as an array of doubles.
     */
    public Optional<double[]> getMetricResult(MetamorphicIndividual individual) {
        return getMetricResults(individual).map(
                mapp -> {
                    var metrics = new double[]{mapp.size()};
                    int i = 0;
                    for(var entry : mapp.entrySet()){
                        metrics[i]=entry.getValue();
                        i++;
                    }
                    return metrics;
                }
        );
    }

    public Optional<Map<Metric,Double>> getMetricResults(MetamorphicIndividual individual){
        return Optional.ofNullable(lookup.get(individual));
    }

    /**
     * Store the current genotype together with the fitness and filename in the map for later reference.
     * @param genotype the list of transformers.
     * @param fileName the file name.
     * @param scores the fitness scores.
     */
    public void storeFiles(MetamorphicIndividual genotype, String fileName, Map<Metric,Double> scores) {
        fileLookup.put(genotype, fileName);
        lookup.put(genotype, scores);
    }

    /**
     * Initialize all weight properties and objectives.
     */
    public void initWeights(boolean max) {
        removeZeroWeights();
        normalizeWeights();

        metricList.forEach(m -> m.setObjective(max));
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

    public void putMetricResults(MetamorphicIndividual i, Map<Metric, Double> inferMetrics) {
        this.lookup.put(i,inferMetrics);
    }

}
