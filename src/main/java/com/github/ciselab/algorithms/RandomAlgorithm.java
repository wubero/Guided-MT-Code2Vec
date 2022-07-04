package com.github.ciselab.algorithms;

import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import com.github.ciselab.support.ParetoFront;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.random.RandomGenerator;

public class RandomAlgorithm {

    private int maxTransformerValue;
    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;
    private final ParetoFront paretoFront;
    private final Logger logger = LogManager.getLogger(RandomAlgorithm.class);


    public String initializeParameters(int maxValue, RandomGenerator randomGenerator) {
        logger.debug("Initialize parameters for the random algorithm");
        maxTransformerValue = maxValue;
        this.randomGenerator = randomGenerator;
        return String.format("{max transformer value: %d}",
                maxValue);
    }

    public RandomAlgorithm(GenotypeSupport gen, ParetoFront paretoFront) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        this.paretoFront = paretoFront;
    }

    public MetamorphicPopulation nextGeneration(MetamorphicPopulation pop) {
        int newLength = pop.getIndividual(0).getLength() + 1;
        logger.debug("Creating a new population of length " + newLength + " through the random algorithm.");
        MetamorphicPopulation newPop = new MetamorphicPopulation(pop.size(), randomGenerator, maxTransformerValue, false, genotypeSupport);
        for(int i = 0; i < pop.size(); i++) {
            MetamorphicIndividual newIndiv = new MetamorphicIndividual(genotypeSupport);
            newIndiv.populateIndividual(randomGenerator, newLength, maxTransformerValue);
            newPop.saveIndividual(i, newIndiv);
        }

        // Check if fitness is already known
        for(MetamorphicIndividual i : newPop.individuals) {
            Optional<double[]> metrics = metricCache.getMetricResult(i.getTransformers());
            if(metrics.isPresent()) {
                i.setMetrics(metrics.get());
            }
        }
        return newPop;
    }

    /**
     * Check population with the current Pareto set.
     * @param population the population
     */
    public void checkPareto(MetamorphicPopulation population) {
        for(int i = 0; i < population.size(); i++) {
            double[] solution = population.getIndividual(i).getMetrics();
            paretoFront.addToParetoOptimum(solution);
        }
    }
}
