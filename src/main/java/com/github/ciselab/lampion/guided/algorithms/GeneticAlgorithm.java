package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * The metamorphic algorithm performs the evolution of the metamorphic populations.
 * A beginners guide to genetic algorithms can be found at
 * https://www.geeksforgeeks.org/simple-genetic-algorithm-sga/#:~:text=Simple%20Genetic%20Algorithm%20(SGA)%20is,each%20of%20the%20solutions%2Findividuals.
 */
public class GeneticAlgorithm {

    /* GA parameters */
    private double crossoverRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;
    private double increaseSizeRate;
    private int maxTransformerValue;
    private int maxGeneLength;
    private int currentGeneration;
    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;
    private final ParetoFront paretoFront;
    private final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);

    /**
     * Initialize all GA parameters.
     * @param uRate the  uniform rate, should be a value between 0 and 1.
     * @param mRate the mutation rate, should be a value between 0 and 1.
     * @param tSize the tournament size, should be a value between 1 and the population size.
     * @param elite elitism, should be true or false.
     * @param increaseRate the increase rate, should be a value between 0 and 1.
     * @param maxValue the maximum transformer value, default should be 7. This is all transformers currently specified
     * @param maxLength the maximum individual length, should always be bigger than 1.
     * @param randomGenerator the random generator.
     * @return the parameters in a string for the logger.
     */
    public String initializeParameters(double uRate, double mRate, int tSize, boolean elite, double increaseRate,
                                              int maxValue, int maxLength, RandomGenerator randomGenerator) {
        logger.debug("Initialize parameters for the genetic algorithm.");
        crossoverRate = uRate;
        mutationRate = mRate;
        tournamentSize = tSize;
        elitism = elite;
        increaseSizeRate = increaseRate;
        maxTransformerValue = maxValue;
        maxGeneLength = maxLength;
        this.randomGenerator = randomGenerator;
        return String.format(Locale.UK,"{mutation rate: %.4f, tournament size: %d, elitism: %b, increase rate: %.4f," +
                " max transformer value: %d, max gene length: %d}",
                (float) uRate, mRate, tSize, elite, increaseRate, maxValue, maxLength);
    }

    /**
     * Constructor for this class.
     * @param genotypeSupport the genotypeSupport.
     * @param paretoFront the pareto front.
     */
    public GeneticAlgorithm(GenotypeSupport genotypeSupport, ParetoFront paretoFront) {
        this.genotypeSupport = genotypeSupport;
        metricCache = genotypeSupport.getMetricCache();
        this.paretoFront = paretoFront;
        currentGeneration = 0;
    }

    /**
     * This method creates the next population with crossover and mutation.
     * @param pop the current population.
     * @return the new metamorphic population
     */
    public MetamorphicPopulation evolvePopulation(MetamorphicPopulation pop) {
        logger.debug("Evolve the old population");
        currentGeneration += 1;
        MetamorphicPopulation newPopulation = new MetamorphicPopulation(pop.size(),
                randomGenerator, maxTransformerValue, false, genotypeSupport, currentGeneration);

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
            // Set parents for new individuals
            for(MetamorphicIndividual individual: newIndividuals) {
                individual.setParents(individual1, individual2);
            }
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

        // Check if fitness is already known, otherwise calculate it
        for(MetamorphicIndividual i : newPopulation.individuals) {
            if (metricCache.getMetricResults(i).isEmpty()){
                metricCache.putMetricResults(i,i.inferMetrics());
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
        logger.debug("Performing crossover");
        MetamorphicIndividual firstIndividual = new MetamorphicIndividual(genotypeSupport, currentGeneration);
        MetamorphicIndividual secondIndividual = new MetamorphicIndividual(genotypeSupport, currentGeneration);
        List<MetamorphicIndividual> individualList = new ArrayList<>();
        // Loop through genes
        for (int i = 0; i < individual1.getLength(); i++) {
            // Crossover
            if (Math.random() <= crossoverRate) {
                firstIndividual.addGene(individual1.getGene(i));
                if (i < individual2.getLength())
                    secondIndividual.addGene(individual2.getGene(i));
            } else {
                if (i < individual2.getLength())
                    firstIndividual.addGene(individual2.getGene(i));
                secondIndividual.addGene(individual1.getGene(i));
            }
        }
        individualList.add(firstIndividual);
        individualList.add(secondIndividual);
        return individualList;
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
                maxTransformerValue, false, genotypeSupport, currentGeneration);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        return tournament.getFittest();
    }


    /**
     * Check population against the current Pareto set.
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
