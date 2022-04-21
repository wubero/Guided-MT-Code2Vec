package com.github.ciselab.program;

import com.github.ciselab.ga.MetamorphicProblem;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.support.GenotypeSupport;
import io.jenetics.BitGene;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.prngine.LCG64ShiftRandom;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This is the main class of the project where the evolutionary algorithm engine is created,
 * together with the MetamorphicProblem.
 */
public class Main {
    private final static int maxTransformerValue = 7;
    private final static int maxTransformers = 10;

    /**
     * The main method for the Guided-MT-Code2Vec project.
     * @param args system arguments.
     */
    public static void main(String[] args) {

        GenotypeSupport.initializeFields();

        final MetamorphicProblem problem = MetamorphicProblem.of(maxTransformerValue, maxTransformers, new LCG64ShiftRandom(101010));

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
                .limit(Limits.bySteadyFitness(3))
                .limit(100)
                .peek(statistics)
                .peek(er -> System.out.println(er.bestPhenotype()))
                .collect(EvolutionResult.toBestPhenotype());

        System.out.println(statistics);
        System.out.println(result);

        System.out.println("System done");
    }

}
