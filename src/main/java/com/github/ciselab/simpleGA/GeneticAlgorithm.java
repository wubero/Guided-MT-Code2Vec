package com.github.ciselab.simpleGA;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.support.GenotypeSupport;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * The metamorphic algorithm performs the evolution of the metamorphic populations.
 */
public class GeneticAlgorithm {

    /* GA parameters */
    private static double uniformRate;
    private static double mutationRate;
    private static int tournamentSize;
    private static boolean elitism;
    private static double increaseSizeRate;
    private static double decreaseSizeRate;
    private static int maxTransformerValue;
    private static int maxGeneLength;
    private static RandomGenerator randomGenerator;


    /**
     * Initialize all GA parameters.
     * @param uRate the  uniform rate.
     * @param mRate the mutation rate.
     * @param tSize the tournament size.
     * @param elite elitism.
     * @param increaseRate the increase rate.
     * @param decreaseRate the decrease rate.
     * @param maxValue the maximum transformer value.
     * @param maxLength the maximum individual length.
     * @param r the random generator.
     * @return the parameters in a string for the logger.
     */
    public static String initializeParameters(double uRate, double mRate, int tSize, boolean elite, double increaseRate,
                                              double decreaseRate, int maxValue, int maxLength, RandomGenerator r) {
        uniformRate = uRate;
        mutationRate = mRate;
        tournamentSize = tSize;
        elitism = elite;
        increaseSizeRate = increaseRate;
        decreaseSizeRate = decreaseRate;
        maxTransformerValue = maxValue;
        maxGeneLength = maxLength;
        randomGenerator = r;
        return String.format("{uniform rate: %g, mutation rate: %g, tournament size: %d, elitism: %b, increase rate: %g," +
                " decrease rate: %g, max transformer value: %d, max gene length: %d}",
                uRate, mRate, tSize, elite, increaseRate, decreaseRate, maxValue, maxLength);
    }

    /**
     * This method creates the next population with crossover and mutation.
     * @param pop the current population.
     * @return the new metamorphic population
     */
    public static MetamorphicPopulation evolvePopulation(MetamorphicPopulation pop) {
        MetamorphicPopulation newPopulation = new MetamorphicPopulation(pop.size(), randomGenerator, maxTransformerValue, false);

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
        for (int i = elitismOffset; i < pop.size(); i++) {
            MetamorphicIndividual indiv1 = tournamentSelection(pop, randomGenerator);
            MetamorphicIndividual indiv2 = tournamentSelection(pop, randomGenerator);
            MetamorphicIndividual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
            refactorSize(newPopulation.getIndividual(i));
        }

        // Check if fitness is already known
        for(MetamorphicIndividual i : newPopulation.individuals) {
            Optional<double[]> metrics = GenotypeSupport.getMetricResult(i.getTransformers());
            if(metrics.isPresent()) {
                i.setMetrics(metrics.get());
            }
        }
        return newPopulation;
    }

    /**
     * Increase the size of an individual by one if the size is not already at the max size.
     * @param indiv The individual to increase the size of.
     */
    private static void refactorSize(MetamorphicIndividual indiv) {
        if (Math.random() <= increaseSizeRate) {
            indiv.increase(maxGeneLength, randomGenerator, maxTransformerValue);
        } else if (Math.random() <= decreaseSizeRate) {
            indiv.decrease(randomGenerator);
        }
    }

    /**
     * Crossover two metamorphic individuals.
     * @param indiv1 the first metamorphic individual.
     * @param indiv2 the second metamorphic individual.
     * @return the new metamorphic individual.
     */
    private static MetamorphicIndividual crossover(MetamorphicIndividual indiv1, MetamorphicIndividual indiv2) {
        MetamorphicIndividual newSol = new MetamorphicIndividual();
        // Loop through genes
        for (int i = 0; i < indiv1.getLength(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.addGene(indiv1.getGene(i));
            } else {
                if (i < indiv2.getLength())
                    newSol.addGene(indiv2.getGene(i));
            }
        }
        return newSol;
    }

    /**
     * Mutate a metamorphic individual by changing a transformer in its genotype.
     * @param indiv the metamorphic individual to mutate.
     */
    private static void mutate(MetamorphicIndividual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.getLength(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                int key = (int) (Math.random() * maxTransformerValue);
                BaseTransformer transformer = indiv.createGene(key, randomGenerator);
                indiv.setGene(i, transformer);
            }
        }
        if (GenotypeSupport.getMetricResult(indiv.getTransformers()).isPresent()) {
            double[] metrics = GenotypeSupport.getMetricResult(indiv.getTransformers()).get();
            indiv.setMetrics(metrics);
        }
    }

    /**
     * This method chooses a number of metamorphic individuals to perform tournament selection on.
     * From these metamorphic individuals it chooses the best metamorphic individual and returns that.
     * @param pop the current population.
     * @param random the random generator used in this run.
     * @return the new metamorphic individual.
     */
    private static MetamorphicIndividual tournamentSelection(MetamorphicPopulation pop, RandomGenerator random) {
        // Create a tournament population
        MetamorphicPopulation tournament = new MetamorphicPopulation(tournamentSize, random, maxTransformerValue, false);
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
    public static void checkPareto(MetamorphicPopulation population) {
        for(int i = 0; i < population.size(); i++) {
            double[] solution = population.getIndividual(i).getMetrics();
            GenotypeSupport.addToParetoOptimum(solution);
        }
    }
}
