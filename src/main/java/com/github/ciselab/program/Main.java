package com.github.ciselab.program;

import com.github.ciselab.simpleGA.GeneticAlgorithm;
import com.github.ciselab.simpleGA.MetamorphicIndividual;
import com.github.ciselab.simpleGA.MetamorphicPopulation;
import com.github.ciselab.support.GenotypeSupport;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main class of the project where the evolutionary algorithm engine is created,
 * together with the MetamorphicProblem.
 */
public class Main {

    // GA parameters
    private final static double uniformRate = 0.7; // 0.7, 0.8, 0.9
    private final static double mutationRate = 0.3; //0.2, 0.3, 0.4
    private final static int tournamentSize = 4;
    private final static boolean elitism = false;
    private final static double increaseSizeRate = 0.7; //0.6, 0.7, 0.8


    private final static int maxTransformerValue = 6;
    private final static int maxGeneLength = 20;
    private static int popSize = 10;
    private static int maxSteadyGenerations = 1;
    private static int maxTimeInMin = 30;
    private final static Logger logger = LogManager.getLogger(Main.class);

    private static String logDir;

    /**
     * The main method for the Guided-MT-Code2Vec project.
     * @param args system arguments.
     */
    public static void main(String[] args) {

        logger.info("Guided-MT started");
        if(args.length == 0) {
            logger.info("No arguments found - loading default values");
        } else if (args.length == 3) {
            logger.info("Received four arguments - Config input: " + args[0] + ", data input: " + args[1]
                    + " and output: " + args[2]);
            GenotypeSupport.setConfigFile(args[0]);
            GenotypeSupport.setDataDir(args[1]);
            logDir = args[2];

        } else {
            logger.error("Received an unknown amount of arguments");
            return;
        }
        logger.info("Configuration: " + GenotypeSupport.initializeFields().toString());
        runSimpleGA();
    }

    /**
     * Setter for the max steady generations stopping criteria, used in the tests.
     * @param generations the maximum number of steady generations.
     */
    public static void setMaxGenerations(int generations) {
        maxSteadyGenerations = generations;
    }

    /**
     * Setter for the population size, used in tests.
     * @param pop the population size.
     */
    public static void setPopSize(int pop) {
        popSize = pop;
    }

    /**
     * Run the custom simple genetic algorithm created for variable length chromosomes.
     */
    public static void runSimpleGA() {
        LocalTime start = LocalTime.now();
        boolean converged = false;
        RandomGenerator random = new SplittableRandom(10110);
        String GA_parameters = GeneticAlgorithm.initializeParameters(uniformRate, mutationRate, tournamentSize, elitism, increaseSizeRate,
                maxTransformerValue, maxGeneLength, random);
        logger.info("GA parameters: " + GA_parameters);

        // Create an initial population
        try {
            MetamorphicPopulation myPop = new MetamorphicPopulation(popSize, random, maxTransformerValue, true);
            logger.debug("Initial population: " + myPop);

            FileWriter myWriter;
            if (logDir == null)
                myWriter = new FileWriter("GA_results.txt");
            else
                myWriter = new FileWriter(logDir + "/GA_results.txt");

            MetamorphicIndividual best = new MetamorphicIndividual();
            double bestFitness = best.getFitness();
            logger.info("Initial fitness without transformations: " + bestFitness);
            logger.info("The metric results corresponding to the transformations are: " + Arrays.toString(best.getMetrics()));

            // check best against pareto
            GenotypeSupport.addToParetoOptimum(best.getMetrics());

            // Evolve our population until we reach an optimum solution
            int generationCount = 0;
            int steadyGens = 0;
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
                GeneticAlgorithm.checkPareto(myPop);
                logger.debug("Current Pareto set = " + displayPareto(GenotypeSupport.getPareto()));

                myWriter.write("Generation: " + generationCount + ", result: " + myPop.getFittest().getFitness() + "\n");
                myWriter.write("Gene: " + best + "\n");

                logger.info("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getFitness() + " Gene:");
                logger.info(best.toString());

                myPop = GeneticAlgorithm.evolvePopulation(myPop);
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

            GeneticAlgorithm.checkPareto(myPop);
            logger.info("Metrics are: " + Arrays.toString(GenotypeSupport.getMetrics().toArray()));
            logger.info("Pareto set: " + displayPareto(GenotypeSupport.getPareto()));
            getVariance();

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

    /**
     * Display the Pareto set for the logger.
     * @param pareto the Pareto set.
     * @return the String that can be displayed in the logger.
     */
    private static String displayPareto(Set<double[]> pareto) {
        String out = "{";
        for(double[] i: pareto)
            out += Arrays.toString(i) + ", ";
        return out.substring(0, out.length()-2) + "}";
    }

    private static void getVariance() {
        double[] variance = new double[GenotypeSupport.getActiveMetrics()];
        double[] means = new double[GenotypeSupport.getActiveMetrics()];
        double[] sum = new double[GenotypeSupport.getActiveMetrics()];
        int size = GenotypeSupport.metricLookup.values().size();

        for(int i = 0; i < GenotypeSupport.getActiveMetrics(); i++)
            sum[i] = 0;
        for(double[] value: GenotypeSupport.metricLookup.values()) {
            for (int i = 0; i < GenotypeSupport.getActiveMetrics(); i++) {
                sum[i] += value[i];
            }
        }
        for(int i = 0; i < GenotypeSupport.getActiveMetrics(); i++)
            means[i] = sum[i]/size;
        logger.info("The metric means are: " + Arrays.toString(means));

        for(int i = 0; i < GenotypeSupport.getActiveMetrics(); i++)
            sum[i] = 0;
        for(double[] value: GenotypeSupport.metricLookup.values()) {
            for (int i = 0; i < GenotypeSupport.getActiveMetrics(); i++) {
                sum[i] += Math.pow((value[i] - means[i]), 2);
            }
        }
        for(int i = 0; i < GenotypeSupport.getActiveMetrics(); i++)
            variance[i] = sum[i]/(size-1);
        logger.info("The metric variance are: " + Arrays.toString(variance));
    }
}
