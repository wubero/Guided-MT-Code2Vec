package com.github.ciselab.simpleGA;

import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * The metamorphic algorithm performs the evolution of the metamorphic populations.
 * A beginners guide to genetic algorithms can be found at
 * https://www.geeksforgeeks.org/simple-genetic-algorithm-sga/#:~:text=Simple%20Genetic%20Algorithm%20(SGA)%20is,each%20of%20the%20solutions%2Findividuals.
 */
public class GeneticAlgorithm {

    /* GA parameters */
    private double uniformRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;
    private double increaseSizeRate;
    private int maxTransformerValue;
    private int maxGeneLength;
    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;

    /**
     * Initialize all GA parameters.
     * @param uRate the  uniform rate.
     * @param mRate the mutation rate.
     * @param tSize the tournament size.
     * @param elite elitism.
     * @param increaseRate the increase rate.
     * @param maxValue the maximum transformer value.
     * @param maxLength the maximum individual length.
     * @param r the random generator.
     * @return the parameters in a string for the logger.
     */
    public String initializeParameters(double uRate, double mRate, int tSize, boolean elite, double increaseRate,
                                              int maxValue, int maxLength, RandomGenerator r) {
        uniformRate = uRate;
        mutationRate = mRate;
        tournamentSize = tSize;
        elitism = elite;
        increaseSizeRate = increaseRate;
        maxTransformerValue = maxValue;
        maxGeneLength = maxLength;
        randomGenerator = r;
        return String.format("{uniform rate: %g, mutation rate: %g, tournament size: %d, elitism: %b, increase rate: %g," +
                " max transformer value: %d, max gene length: %d}",
                uRate, mRate, tSize, elite, increaseRate, maxValue, maxLength);
    }

    public GeneticAlgorithm(GenotypeSupport gen) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
    }

    /**
     * This method creates the next population with crossover and mutation.
     * @param pop the current population.
     * @return the new metamorphic population
     */
    public MetamorphicPopulation evolvePopulation(MetamorphicPopulation pop) {
        MetamorphicPopulation newPopulation = new MetamorphicPopulation(pop.size(),
                randomGenerator, maxTransformerValue, false, genotypeSupport);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        int index = elitismOffset;
        while (index < newPopulation.size()) {
            MetamorphicIndividual individual1 = tournamentSelection(pop, randomGenerator);
            MetamorphicIndividual individual2 = tournamentSelection(pop, randomGenerator);
            List<MetamorphicIndividual> newIndividuals = crossover(individual1, individual2);
            newPopulation.saveIndividual(index, newIndividuals.get(0));
            index++;
            if (index < newPopulation.size()) {
                newPopulation.saveIndividual(index, newIndividuals.get(1));
                index++;
            }
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            if( Math.random() <= mutationRate)
                mutate(newPopulation.getIndividual(i));
        }

        // Check if fitness is already known
        for(MetamorphicIndividual i : newPopulation.individuals) {
            Optional<double[]> metrics = metricCache.getMetricResult(i.getTransformers());
            if(metrics.isPresent()) {
                i.setMetrics(metrics.get());
            }
        }
        return newPopulation;
    }

    /**
     * Mutate the current individual
     * @param individual The individual to increase or decrease the size of.
     */
    private void mutate(MetamorphicIndividual individual) {
        if (Math.random() <= increaseSizeRate) {
            individual.increase(maxGeneLength, randomGenerator, maxTransformerValue);
        } else {
            individual.decrease(randomGenerator);
        }
    }

    /**
     * Crossover two metamorphic individuals.
     * @param individual1 the first metamorphic individual.
     * @param individual2 the second metamorphic individual.
     * @return the new metamorphic individual.
     */
    private List<MetamorphicIndividual> crossover(MetamorphicIndividual individual1, MetamorphicIndividual individual2) {
        MetamorphicIndividual newSol = new MetamorphicIndividual(genotypeSupport);
        MetamorphicIndividual newSol2 = new MetamorphicIndividual(genotypeSupport);
        List<MetamorphicIndividual> l = new ArrayList<>();
        // Loop through genes
        for (int i = 0; i < individual1.getLength(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.addGene(individual1.getGene(i));
                if (i < individual2.getLength())
                    newSol2.addGene(individual2.getGene(i));
            } else {
                if (i < individual2.getLength())
                    newSol.addGene(individual2.getGene(i));
                newSol2.addGene(individual1.getGene(i));
            }
        }
        l.add(newSol);
        l.add(newSol2);
        return l;
    }

    /**
     * This method chooses a number of metamorphic individuals to perform tournament selection on.
     * From these metamorphic individuals it chooses the best metamorphic individual and returns that.
     * @param pop the current population.
     * @param random the random generator used in this run.
     * @return the new metamorphic individual.
     */
    private MetamorphicIndividual tournamentSelection(MetamorphicPopulation pop, RandomGenerator random) {
        // Create a tournament population
        MetamorphicPopulation tournament = new MetamorphicPopulation(tournamentSize, random,
                maxTransformerValue, false, genotypeSupport);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        return tournament.getFittest();
    }

    /**
     * Check population with the current Pareto set.
     * @param population the population
     */
    public void checkPareto(MetamorphicPopulation population) {
        for(int i = 0; i < population.size(); i++) {
            double[] solution = population.getIndividual(i).getMetrics();
            genotypeSupport.addToParetoOptimum(solution);
        }
    }
}
