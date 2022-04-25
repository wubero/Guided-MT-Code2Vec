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
import java.util.random.RandomGenerator;

/**
 * This is the main class of the project where the evolutionary algorithm engine is created,
 * together with the MetamorphicProblem.
 */
public class Main {

    private final static int maxTransformerValue = 7;
    private final static int popSize = 5;
    private static final int maxSteadyGenerations = 15;

    /**
     * The main method for the Guided-MT-Code2Vec project.
     * @param args system arguments.
     */
    public static void main(String[] args) {

        GenotypeSupport.initializeFields();

        //runJeneticsGA();
        runSimpleGA();

        System.out.println("System done");
    }

    /**
     * Run the custom simple genetic algorithm created for variable length chromosomes.
     */
    private static void runSimpleGA() {
        boolean converged = false;
        RandomGenerator random = new LCG64ShiftRandom(101010);

        // Create an initial population
        try {
            MetamorphicPopulation myPop = new MetamorphicPopulation(popSize, random, maxTransformerValue, true);
            FileWriter myWriter = new FileWriter("GA_results.txt");

            // Evolve our population until we reach an optimum solution
            int generationCount = 0;
            MetamorphicIndividual best = new MetamorphicIndividual();
            double bestFitness = best.getFitness();
            int steadyGens = 0;
            while (!converged) {
                generationCount++;
                if (isFitter(myPop, bestFitness)) {
                    bestFitness = myPop.getFittest().getFitness();
                    best = myPop.getFittest();
                    steadyGens = 0;
                } else {
                    steadyGens++;
                }
                if (steadyGens > maxSteadyGenerations)
                    converged = true;

                myWriter.write("Generation: " + generationCount + ", result: " + myPop.getFittest().getFitness() + "\n");
                myWriter.write("Gene: " + best);


                System.out.println("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getFitness() + " Gene:");
                System.out.println(best);
                myPop = MetamorphicAlgorithm.evolvePopulation(myPop, random);
            }
            System.out.println("Generation: " + generationCount);
            System.out.println("Max fitness: " + best.getFitness());
            System.out.println("Genes:");
            System.out.println(best);
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

    private static boolean isFitter(MetamorphicPopulation pop, double best) {
        if(GenotypeSupport.maximize) {
            return pop.getFittest().getFitness() > best;
        } else {
            return pop.getFittest().getFitness() < best;
        }
    }

}
