package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.guided.configuration.GeneticConfiguration;
import com.github.ciselab.lampion.guided.program.Main;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    GeneticConfiguration config;

    private RandomGenerator randomGenerator;
    private final GenotypeSupport genotypeSupport;
    private final MetricCache metricCache;
    private final ParetoFront paretoFront;
    private final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    private int currentGeneration;

    /**
     * Constructor for this class.
     * @param genotypeSupport the genotypeSupport.
     * @param paretoFront the pareto front.
     */
    public GeneticAlgorithm(GeneticConfiguration config, MetricCache cache, GenotypeSupport genotypeSupport, ParetoFront paretoFront, RandomGenerator generator) {
        this.genotypeSupport = genotypeSupport;
        this.randomGenerator = generator;
        this.config = config;
        metricCache = cache;
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
                randomGenerator, Main.maxTransformerValue, false, genotypeSupport, currentGeneration);

        // Loop over the population size and create new individuals with
        // crossover
        int index = 0;
        while (index < newPopulation.size()) {
            MetamorphicIndividual individual1 = tournamentSelection(pop, randomGenerator).get();
            MetamorphicIndividual individual2 = tournamentSelection(pop, randomGenerator).get();
            List<MetamorphicIndividual> newIndividuals = crossover(individual1, individual2);
            // Set parents for new individuals
            for(MetamorphicIndividual individual: newIndividuals) {
                individual.setParents(individual1, individual2);
            }
            newPopulation.saveIndividual(newIndividuals.get(0));
            index++;
            if (index < newPopulation.size()) {
                newPopulation.saveIndividual(newIndividuals.get(1));
                index++;
            }
        }

        // Mutate population
        for (int i = 0; i < newPopulation.size(); i++) {
            if( Math.random() <= config.getMutationRate())
                mutate(newPopulation.getIndividual(i).get());
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
        if (Math.random() <= config.getIncreaseSizeRate()) {
            individual.increase(config.getMaxGeneLength(), randomGenerator, Main.maxTransformerValue);
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
            if (Math.random() <= config.getCrossoverRate()) {
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
    private Optional<MetamorphicIndividual> tournamentSelection(MetamorphicPopulation pop, RandomGenerator random) {
        // Create a tournament population
        MetamorphicPopulation tournament = new MetamorphicPopulation(config.getTournamentSize(), random,
                Main.maxTransformerValue, false, genotypeSupport, currentGeneration);
        // For each place in the tournament get a random individual
        for (int i = 0; i < config.getTournamentSize(); i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(pop.getIndividual(randomId).get());
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
            paretoFront.addToParetoOptimum(population.getIndividual(i).get());
        }
    }

}
