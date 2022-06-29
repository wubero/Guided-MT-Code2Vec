package com.github.ciselab.simpleGA;

import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import com.github.ciselab.support.Pareto;

import java.util.Optional;
import java.util.random.RandomGenerator;

public class RandomAlgorithm {

    private int maxTransformerValue;
    private int maxGeneLength;
    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;
    private final Pareto pareto;


    public String initializeParameters(int maxValue, int maxLength, RandomGenerator r) {
        maxTransformerValue = maxValue;
        maxGeneLength = maxLength;
        randomGenerator = r;
        return String.format("{max transformer value: %d, max gene length: %d}",
                maxValue, maxLength);
    }

    public RandomAlgorithm(GenotypeSupport gen, Pareto pareto) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        this.pareto = pareto;
    }

    public MetamorphicPopulation nextGeneration(MetamorphicPopulation pop) {
        int newLength = pop.getIndividual(0).getLength() + 1;
        MetamorphicPopulation newPop = new MetamorphicPopulation(pop.size(), randomGenerator, maxTransformerValue, false, genotypeSupport);
        for(int i = 0; i < pop.size(); i++) {
            MetamorphicIndividual newIndiv = new MetamorphicIndividual(genotypeSupport);
            newIndiv.createIndividual(randomGenerator, newLength, maxTransformerValue);
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
            pareto.addToParetoOptimum(solution);
        }
    }
}
