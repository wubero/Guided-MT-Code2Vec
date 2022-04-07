package com.github.ciselab.program;

import com.github.ciselab.ga.MetamorphicProblem;
import io.jenetics.EnumGene;
import io.jenetics.Mutator;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.prngine.LCG64ShiftRandom;
import org.apache.commons.lang3.tuple.Pair;

public class Main {

    public static void main(String[] args) {
        PipelineSupport.initialize();

        final MetamorphicProblem problem = MetamorphicProblem.of(3, 10, new LCG64ShiftRandom(101010));

        final Engine<EnumGene<Pair<Integer, Integer>>, Double> engine = Engine.builder(problem)
                .minimizing()
                .maximalPhenotypeAge(5)
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
