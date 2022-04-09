package com.github.ciselab.program;

import com.github.ciselab.ga.MetamorphicProblem;
import com.github.ciselab.support.PipelineSupport;
import io.jenetics.EnumGene;
import io.jenetics.Mutator;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.prngine.LCG64ShiftRandom;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This is the main class of the project where the evolutionary algorithm engine is created,
 * together with the MetamorphicProblem.
 */
public class Main {

    /**
     * The main method for the Guided-MT-Code2Vec project.
     * @param args system arguments.
     */
    public static void main(String[] args) {
        PipelineSupport.initializeFields();

        final MetamorphicProblem problem = MetamorphicProblem.of(3, 3, new LCG64ShiftRandom(101010));

        final Engine<EnumGene<Pair<Integer, Integer>>, Double> engine = Engine.builder(problem)
                .maximizing()
                .maximalPhenotypeAge(3)
                .alterers(
                        new PartiallyMatchedCrossover<>(0.4),
                        new Mutator<>(0.3))
                .build();

        final Phenotype<EnumGene<Pair<Integer, Integer>>, Double> result = engine.stream()
                .limit(Limits.bySteadyFitness(5))
                .collect(EvolutionResult.toBestPhenotype());

        System.out.print(result);
    }

}
