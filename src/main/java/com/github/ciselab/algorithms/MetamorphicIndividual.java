package com.github.ciselab.algorithms;

import com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.AddUnusedVariableTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfFalseElseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.LambdaIdentityTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RenameVariableTransformer;
import com.github.ciselab.metric.Metric;
import com.github.ciselab.metric.metrics.InputLength;
import com.github.ciselab.metric.metrics.Transformations;
import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class creates a metamorphic individual for the metamorphic population.
 * It can increase the size or change the genotype of this metamorphic individual.
 */
public class MetamorphicIndividual {

    private final Logger logger = LogManager.getLogger(MetamorphicIndividual.class);
    private GenotypeSupport genotypeSupport;
    private MetricCache metricCache;

    // Path to where the altered Java Files are Stored (empty until transformers are run)
    protected Optional<String> javaPath = Optional.empty();
    // Path to where the output of Code2Vec is stored for this Individual (empty if Code2Vec was not run)
    protected Optional<String> resultPath = Optional.empty();

    private List<BaseTransformer> transformers = new ArrayList<>();
    private Optional<Double> fitness = Optional.empty(); // Empty while not calculated or reset
    private Map<Metric,Double> metrics;

    public MetamorphicIndividual (GenotypeSupport gen) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        metrics = new HashMap<>();
    }

    /**
     * Populate the current metamorphic individual.
     * The key and seed are randomly generated according to the maximum transformer value and the random generator.
     * @param randomGenerator the random generator used for this run.
     * @param length the length.
     * @param maxTransformerValue the maximum transformer key value.
     */
    public void populateIndividual(RandomGenerator randomGenerator, int length, int maxTransformerValue) {
        transformers.clear();
        metrics = new HashMap<>();
        for(int i = 0; i < length; i++) {
            int key = randomGenerator.nextInt(0, maxTransformerValue+1);
            int seed = randomGenerator.nextInt();
            transformers.add(genotypeSupport.createTransformers(key, seed));
        }
    }

    /**
     * Get the length of this metamorphic individual.
     * @return the length.
     */
    public int getLength() {
        return transformers.size();
    }

    public Optional<String> getJavaPath(){return this.javaPath;}
    public void setJavaPath(String path){this.javaPath = Optional.of(path);}

    public Optional<String> getResultPath(){return this.resultPath;}
    public void setResultPath(String path){this.resultPath = Optional.of(path);}

    /**
     * Get the transformer at an index.
     * @param index the index.
     * @return the transformer.
     */
    public BaseTransformer getGene(int index) {
        return transformers.get(index);
    }

    /**
     * Get the transformers for this metamorphic individual.
     * @return the list of transformers.
     */
    public List<BaseTransformer> getTransformers() {
        return transformers;
    }

    /**
     * Change the transformer at an index.
     * @param index the index.
     * @param gene the transformer.
     */
    public void setGene(int index, BaseTransformer gene) {
        transformers.set(index, gene);
        fitness = Optional.empty();
    }

    /**
     * Add a new transformer to the list and set the fitness back to -1;
     * @param gene the transformer to add.
     */
    public void addGene(BaseTransformer gene) {
        transformers.add(gene);
        fitness = Optional.empty();
    }

    /**
     * If the length is not max, increase the length of this metamorphic individual by one.
     * @param maxGeneLength the max length for the transformer list.
     * @param randomGen the random generator used for this run.
     * @param maxValue the maximum value for the transformer keys.
     */
    public void increase(int maxGeneLength, RandomGenerator randomGen, int maxValue) {
        if(getLength() < maxGeneLength) {
            BaseTransformer newTransformer = genotypeSupport.createTransformers(randomGen.nextInt(1, maxValue+1), randomGen.nextInt());
            fitness = Optional.empty();
            if(metricCache.getDir(transformers).isPresent()) {
                List<BaseTransformer> t = new ArrayList<>();
                t.add(newTransformer);
                String oldDir = metricCache.getDir(transformers).get() + "/test";
                String name = genotypeSupport.runTransformations(t, oldDir);
                determineFitness(name);
                transformers.add(newTransformer);
                metricCache.storeFiles(this, name, metrics);
            } else {
                transformers.add(newTransformer);
                if (metricCache.getMetricResult(this).isPresent()) {
                    metrics = metricCache.getMetricResults(this).get();
                    fitness = Optional.of(calculateFitness());
                }
            }
            logger.debug("The gene " + this.hashCode() + " has increased its size to " + this.getLength());
        }
    }

    /**
     * Calculate fitness according to the directory name.
     * @param name the directory name.
     */
    private void determineFitness(String name) {
        genotypeSupport.runCode2vec(name);
        double[] primary = calculateMetric();
        double[] secondary = secondaryMetrics(name);
        int j = 0;
        for(int i = 0; i < metrics.length; i++) {
            if(i < primary.length)
                metrics[i] = primary[i];
            else {
                metrics[i] = secondary[j];
                j++;
            }
        }
        fitness = calculateFitness(metrics);
    }

    /**
     * Decrease the amount of transformers for this metamorphic individual.
     * @param randomGen the random generator used for this run.
     */
    public void decrease(RandomGenerator randomGen) {
        if(getLength() > 1) {
            int drop = randomGen.nextInt(0, getLength());
            transformers.remove(drop);
            logger.debug("The gene " + this.hashCode() + " has decreased its size to " + this.getLength());
            if(metricCache.getMetricResults(this).isPresent()) {
                metrics = metricCache.getMetricResults(this).get();
                fitness = calculateFitness(metrics);
            } else {
                fitness = Optional.empty();
                metrics = new HashMap<>();
            }
        }
    }

    /**
     * Get the list of scores for ever data point in the dataset for every metric.
     * @return the list of all scores for every metric.
     */
    public Map<String, List<Float>> getScoresList() {
        Map<String, List<Float>> metricScores = new HashMap<>();
        for(Metric metric: metricCache.getMetrics()) {
            metricScores.put(metric.getName(), metric.getScores());
        }
        return metricScores;
    }

    /**
     * Create a new gene, transformer, for the metamorphic individual.
     * @param key the key for the transformer.
     * @param random the random generator used for this run.
     * @return the transformer created.
     */
    public BaseTransformer createGene(int key, RandomGenerator random) {
        return genotypeSupport.createTransformers(key, random.nextInt());
    }

    /**
     * Get the fitness of this metamorphic individual. If it does not exist calculate it.
     * Every metric has a certain weight specified in the config. These weights are normalized for all included metrics.
     * The score of a weight is then multiplied by its weights and added to the fitness of this individual.
     * The resulting fitness of all metrics will be between 0 and 1.
     * @return the fitness of this metamorphic individual.
     */
    public double getFitness() {
        if (fitness.isEmpty() || fitness.get() < 0.0) {
            String name = genotypeSupport.runTransformations(transformers, genotypeSupport.getCurrentDataset());
            determineFitness(name);
            metricCache.fillFitness(this, metrics);
        }
        logger.debug("The gene " + this.hashCode() + " has calculated its fitness, it is: " + fitness);
        return fitness.get();
    }


    /**
     * Set the metrics of this metamorphic individual.
     * @param metrics the metrics to set.
     */
    public void setMetrics(Map<Metric,Double> results) {
        this.metrics = results;
        fitness = Optional.of(calculateFitness());
    }

    public void setMetric(Metric m, double d){
        metrics.put(m,d);
    }

    /**
     * Calculate the global fitness of the metrics with the weights for each metric.
     * @return The global fitness.
     */
    private double calculateFitness() {
        return metricCache.getActiveMetrics().stream().mapToDouble(
                m -> m.apply(this) * m.getWeight()
        ).sum();
    }

    @Override
    public String toString() {
        String geneString = "[";
        for (BaseTransformer i: transformers) {
            String[] temp = i.getClass().toString().split("\\.");
            String addition = temp[temp.length-1];
            geneString += addition + ", ";
        }
        if(geneString.length() < 5) {
            return "[]";
        } else {
            return geneString.substring(0, geneString.length() - 2) + "]";
        }
    }
}
