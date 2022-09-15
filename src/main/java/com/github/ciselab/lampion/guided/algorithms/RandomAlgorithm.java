package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Optional;
import java.util.random.RandomGenerator;

public class RandomAlgorithm {

    private int maxTransformerValue;
    private int currentGeneration;
    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;
    private final ParetoFront paretoFront;
    private final Logger logger = LogManager.getLogger(RandomAlgorithm.class);


    public String initializeParameters(int maxValue, RandomGenerator randomGenerator) {
        logger.debug("Initialize parameters for the random algorithm");
        maxTransformerValue = maxValue;
        this.randomGenerator = randomGenerator;
        return String.format(Locale.UK,"{max transformer value: %d}", maxValue);
    }

    public RandomAlgorithm(GenotypeSupport gen, ParetoFront paretoFront) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        this.paretoFront = paretoFront;
        currentGeneration = 0;
    }

    public MetamorphicPopulation nextGeneration(MetamorphicPopulation pop) {
        int newLength = pop.getIndividual(0).getLength() + 1;
        currentGeneration += 1;
        logger.debug("Creating a new population of length " + newLength + " through the random algorithm.");
        MetamorphicPopulation newPop = new MetamorphicPopulation(pop.size(), randomGenerator, maxTransformerValue, false, genotypeSupport, currentGeneration);
        for(int i = 0; i < pop.size(); i++) {
            MetamorphicIndividual newIndiv = new MetamorphicIndividual(genotypeSupport, currentGeneration);
            newIndiv.populateIndividual(randomGenerator, newLength, maxTransformerValue);
            newPop.saveIndividual(i, newIndiv);
        }

        // Check if fitness is already known, otherwise calculate it
        for(MetamorphicIndividual i : newPop.individuals) {
            if (metricCache.getMetricResults(i).isEmpty()){
                metricCache.putMetricResults(i,i.inferMetrics());
            }
        }
        return newPop;
    }

    /**
     * Check population with the current Pareto set.
     * @param population the population
     */
    public void checkPareto(MetamorphicPopulation population) {
        // This has to be an iteration, as the Pareto Front is maybe altered in during the run.
        // Hence, it has to be done step by step otherwise you get a concurrentmodificationexception
        for(int i = 0; i < population.size(); i++) {
            paretoFront.addToParetoOptimum(population.getIndividual(i));
        }
    }
}
