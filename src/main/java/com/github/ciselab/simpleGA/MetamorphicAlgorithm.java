package com.github.ciselab.simpleGA;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.support.GenotypeSupport;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * The metamorphic algorithm performs the evolution of the metamorphic populations.
 */
public class MetamorphicAlgorithm {

    /* GA parameters */
    private static final double uniformRate = 0.8;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 4;
    private static final boolean elitism = true;
    private static final double increaseSizeRate = 0.4;
    private static final double decreaseSizeRate = 0.7;
    private static final int maxTransformerValue = 6;
    private static int maxGeneLength = 10;
    private static RandomGenerator randomGenerator;


    /**
     * This method creates the next population with crossover and mutation.
     * @param pop the current population.
     * @param random the random generator used in this run.
     * @return the new metamorphic population
     */
    public static MetamorphicPopulation evolvePopulation(MetamorphicPopulation pop, RandomGenerator random) {
        randomGenerator = random;
        MetamorphicPopulation newPopulation = new MetamorphicPopulation(pop.size(), random, maxTransformerValue, false);

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
            MetamorphicIndividual indiv1 = tournamentSelection(pop, random);
            MetamorphicIndividual indiv2 = tournamentSelection(pop, random);
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
            Optional<Double> fitness = GenotypeSupport.getMetricResult(i.getTransformers());
            if(fitness.isPresent()) {
                i.setFitness(fitness.get());
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
            indiv.setFitness(GenotypeSupport.getMetricResult(indiv.getTransformers()).get());
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
}
