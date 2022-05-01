package com.github.ciselab.program;

import com.github.ciselab.simpleGA.MetamorphicAlgorithm;
import com.github.ciselab.simpleGA.MetamorphicIndividual;
import com.github.ciselab.simpleGA.MetamorphicPopulation;
import com.github.ciselab.jeneticsGA.MetamorphicProblem;
import com.github.ciselab.support.GenotypeSupport;
import io.jenetics.BitGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.prngine.LCG64ShiftRandom;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.random.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main class of the project where the evolutionary algorithm engine is created,
 * together with the MetamorphicProblem.
 */
public class Main {

    // GA parameters
    private final static double uniformRate = 0.8;
    private final static double mutationRate = 0.015;
    private final static int tournamentSize = 4;
    private final static boolean elitism = true;
    private final static double increaseSizeRate = 0.4;
    private final static double decreaseSizeRate = 0.7;
    private final static int maxTransformerValue = 6;
    private final static int maxGeneLength = 10;

    private final static int popSize = 5;
    private static final int maxSteadyGenerations = 25;
    private static int maxTimeInMin = 900;
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * The main method for the Guided-MT-Code2Vec project.
     * @param args system arguments.
     */
    public static void main(String[] args) {

        logger.info("Guided-MT started");
        logger.info("Configuration: " + GenotypeSupport.initializeFields().toString());

        //runJeneticsGA();
        runSimpleGA();
    }

    /**
     * Run the custom simple genetic algorithm created for variable length chromosomes.
     */
    private static void runSimpleGA() {
        LocalTime start = LocalTime.now();
        boolean converged = false;
        RandomGenerator random = new LCG64ShiftRandom(101010);
        String GA_parameters = MetamorphicAlgorithm.initializeParameters(uniformRate, mutationRate, tournamentSize, elitism, increaseSizeRate,
                decreaseSizeRate, maxTransformerValue, maxGeneLength, random);
        logger.info("GA parameters: " + GA_parameters);

        // Create an initial population
        try {
            MetamorphicPopulation myPop = new MetamorphicPopulation(popSize, random, maxTransformerValue, true);
            FileWriter myWriter = new FileWriter("GA_results.txt");

            // Evolve our population until we reach an optimum solution
            int generationCount = 0;
            MetamorphicIndividual best = new MetamorphicIndividual();
            double bestFitness = best.getFitness();
            logger.info("Initial fitness without transformations: " + bestFitness);
            int steadyGens = 0;
            logger.debug("Initial population: " + myPop);
            while (!converged && timeDiffSmaller(start)) {
                generationCount++;
                logger.info("Generation " + generationCount);
                if (isFitter(myPop, bestFitness)) {
                    bestFitness = myPop.getFittest().getFitness();
                    best = myPop.getFittest();
                    steadyGens = 0;
                } else
                    steadyGens++;
                if (steadyGens > maxSteadyGenerations)
                    converged = true;

                myWriter.write("Generation: " + generationCount + ", result: " + myPop.getFittest().getFitness() + "\n");
                myWriter.write("Gene: " + best + "\n");

                logger.info("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getFitness() + " Gene:");
                logger.info(best.toString());
                myPop = MetamorphicAlgorithm.evolvePopulation(myPop);
                logger.debug("Population of generation " + generationCount + " = " + myPop);
            }
            logger.info("Program finished");
            if(converged)
                logger.info("Terminated because too many steady generations.");
            else
                logger.info("Terminated because total minutes increased max.");
            logger.info("Generation used: " + generationCount);
            logger.info("Max fitness: " + best.getFitness());
            logger.info("Best individual: ");
            logger.info(best.toString());

            long code2vecTime = GenotypeSupport.getTotalCode2vevTime();
            int code2vecSec = (int) (code2vecTime % 60);
            int code2vecMin = (int) ((code2vecTime / 60)%60);
            logger.info("Total time spent on Code2Vec inference was " + code2vecMin + " minutes and " + code2vecSec + " seconds.");

            long transitionTime = GenotypeSupport.getTotalTransformationTime();
            int transitionSec = (int) (transitionTime % 60);
            int transitionMin = (int) ((transitionTime / 60)%60);
            logger.info("Total time spent on Transformation operations was " + transitionMin + " minutes and " + transitionSec + " seconds.");

            GenotypeSupport.removeOtherDirs();
            logger.info("Clean up other files.");

            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run genetic algorithm from Jenetics package.
     */
    private static void runJeneticsGA() {
        final MetamorphicProblem problem = MetamorphicProblem.of(maxTransformerValue, popSize, new LCG64ShiftRandom(101010));

        final Engine<BitGene, Double> engine = Engine.builder(problem)
                .maximizing()
                .maximalPhenotypeAge(3)
                .populationSize(4)
                .survivorsSelector(new TournamentSelector<>(2))
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.115),
                        new SinglePointCrossover<>(0.16))
                .build();

        // Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?>
                statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Double> result = engine.stream()
                .limit(Limits.bySteadyFitness(maxSteadyGenerations))
                .limit(100)
                .peek(statistics)
                .peek(er -> System.out.println(er.bestPhenotype()))
                .collect(EvolutionResult.toBestPhenotype());

        System.out.println(statistics);
        System.out.println(result);
    }

    /**
     * Determine whether a population is fitter than the current best.
     * @param pop the population.
     * @param best the current best.
     * @return whether the population is fitter.
     */
    public static boolean isFitter(MetamorphicPopulation pop, double best) {
        if(GenotypeSupport.maximize) {
            return pop.getFittest().getFitness() > best;
        } else {
            return pop.getFittest().getFitness() < best;
        }
    }

    /**
     * Calculate whether the amount of minutes between a start date and now is less than the threshold.
     * @param start the start time.
     * @return whether the difference is less than the threshold.
     */
    public static boolean timeDiffSmaller(LocalTime start) {
        long minutesBetween = Duration.between(start, LocalTime.now()).getSeconds() / 60;
        return minutesBetween < maxTimeInMin;
    }

    /**
     * Setter for the maxTimeInMin field.
     * @param time the time to set.
     */
    public static void setMaxTimeInMin(int time) {
        maxTimeInMin = time;
    }

}
