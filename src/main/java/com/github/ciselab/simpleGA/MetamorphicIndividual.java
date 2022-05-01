package com.github.ciselab.simpleGA;

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
import com.github.ciselab.support.GenotypeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates a metamorphic individual for the metamorphic population.
 * It can increase the size or change the genotype of this metamorphic individual.
 */
public class MetamorphicIndividual {

    private static Logger logger = LoggerFactory.getLogger(MetamorphicIndividual.class);
    private int length = 0;
    private List<BaseTransformer> transformers = new ArrayList<>();
    private double fitness = -1;

    /**
     * Create a new metamorphic individual.
     * @param r the random generator used for this run.
     * @param length the length.
     * @param maxTransformerValue the maximum transformer key value.
     */
    public void createIndividual(RandomGenerator r, int length, int maxTransformerValue) {
        transformers.clear();
        this.length = length;
        for(int i = 0; i < length; i++) {
            int key = r.nextInt(1, maxTransformerValue+1);
            int seed = r.nextInt();
            transformers.add(createTransformers(key, seed));
        }
    }

    /**
     * Get the length of this metamorphic individual.
     * @return the length.
     */
    public int getLength() {
        return length;
    }

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
        length = transformers.size();
        fitness = -1;
    }

    /**
     * Add a new transformer to the list and set the fitness back to -1;
     * @param gene the transformer to add.
     */
    public void addGene(BaseTransformer gene) {
        transformers.add(gene);
        length++;
        fitness = -1;
    }

    /**
     * If the length is not max, increase the length of this metamorphic individual by one.
     * @param maxGeneLength the max length for the transformer list.
     * @param randomGen the random generator used for this run.
     * @param maxValue the maximum value for the transformer keys.
     */
    public void increase(int maxGeneLength, RandomGenerator randomGen, int maxValue) {
        if(length < maxGeneLength) {
            int key = randomGen.nextInt(1, maxValue+1);
            int seed = randomGen.nextInt();
            BaseTransformer newTransformer = createTransformers(key, seed);
            length++;
            fitness = -1;
            if(GenotypeSupport.getDir(transformers).isPresent()) {
                List<BaseTransformer> t = new ArrayList<>();
                t.add(newTransformer);
                String oldDir = GenotypeSupport.getDir(transformers).get() + "/test";
                String name = GenotypeSupport.runTransformations(t, oldDir);
                GenotypeSupport.runCode2vec(name);
                fitness = calculateFitness(calculateMetric(name));
                transformers.add(newTransformer);
                GenotypeSupport.storeFiles(transformers, name, fitness);
            } else {
                transformers.add(newTransformer);
                if (GenotypeSupport.getMetricResult(transformers).isPresent()) {
                    fitness = GenotypeSupport.getMetricResult(transformers).get();
                }
            }
            logger.info("The gene " + this.hashCode() + " has increased its size to " + this.length);
        }
    }

    /**
     * Decrease the amount of transformers for this metamorphic individual.
     * @param randomGen the random generator used for this run.
     */
    public void decrease(RandomGenerator randomGen) {
        if(length > 1) {
            int drop = randomGen.nextInt(0, length);
            transformers.remove(drop);
            length--;
            logger.info("The gene " + this.hashCode() + " has decreased its size to " + this.length);
            if(GenotypeSupport.getMetricResult(transformers).isPresent())
                fitness = GenotypeSupport.getMetricResult(transformers).get();
            else
                fitness = -1;
        }
    }

    /**
     * Create a new gene, transformer, for the metamorphic individual.
     * @param key the key for the transformer.
     * @param random the random generator used for this run.
     * @return the transformer created.
     */
    public BaseTransformer createGene(int key, RandomGenerator random) {
        return createTransformers(key, random.nextInt());
    }

    /**
     * Get the fitness of this metamorphic individual. If it does not exist calculate it.
     * @return the fitness of this metamorphic individual.
     */
    public double getFitness() {
        if (fitness < 0) {
            String name = GenotypeSupport.runTransformations(transformers, GenotypeSupport.getCurrentDataset());
            GenotypeSupport.runCode2vec(name);
            fitness = calculateFitness(calculateMetric(name));
            GenotypeSupport.fillFitness(transformers, fitness);
        }
        logger.info("The gene " + this.hashCode() + " has calculated its fitness, it is: " + fitness);
        return fitness;
    }

    /**
     * Set the fitness of this metamorphic individual.
     * @param fitness the fitness to set.
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Calculate the scores for each metric.
     * @return a list of scores.
     */
    private static List<Double> calculateMetric(String dataset) {
        List<Double> scores = new ArrayList<>();
        for(Metric metric: GenotypeSupport.getMetrics()) {
            if(metric.getName().equals("Input_length"))
                InputLength.setDataSet(dataset);
            double score = metric.CalculateScore();
            System.out.println(metric.getName() + ": " + score);
            scores.add(score);
        }
        return scores;
    }

    /**
     * Calculate the global fitness of the metrics with the weights for each metric.
     * @param metrics the list of metrics.
     * @return The global fitness.
     */
    private static double calculateFitness(List<Double> metrics) {
        List<Double> weights = GenotypeSupport.getWeights();
        double output = 0;
        for(int i = 0; i < metrics.size(); i++) {
            output += metrics.get(i)*weights.get(i);
        }
        return output;
    }

    /**
     * Create the transformer based on the key and seed specified.
     * @param key the transformer key.
     * @param seed the transformer seed.
     * @return a transformer that extends the BaseTransformer.
     */
    private static BaseTransformer createTransformers(Integer key, Integer seed) {
        switch (key) {
            case 0:
                return new IfTrueTransformer(seed);
            case 1:
                return new IfFalseElseTransformer(seed);
            case 2:
                return new RenameVariableTransformer(seed);
            case 3:
                return new AddNeutralElementTransformer(seed);
            case 4:
                return new AddUnusedVariableTransformer(seed);
            case 5:
                return new RandomParameterNameTransformer(seed);
            case 6:
                return new LambdaIdentityTransformer(seed);
            default:
                logger.error("The key provided does not match a transformer");
                throw new IllegalArgumentException("The key provided does not match a transformer.");
        }
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
