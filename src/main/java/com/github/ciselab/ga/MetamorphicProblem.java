package com.github.ciselab.ga;

import static java.util.Objects.requireNonNull;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.EmptyMethodTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfFalseElseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RenameVariableTransformer;
import com.github.ciselab.metric.Metric;
import com.github.ciselab.metric.MetricCategory;
import com.github.ciselab.metric.metrics.F1_score;
import com.github.ciselab.metric.metrics.MRR;
import com.github.ciselab.metric.metrics.Percentage_MRR;
import com.github.ciselab.support.PipelineSupport;
import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The MetamorphicProblem class implements the Jenetics package problem interface.
 * It does this by implementing a sequence of transformer key and seed pairs. For each of these pairs a transformer is built.
 * These transformers are then performed on the test dataset in the order in which they are described.
 */
public class MetamorphicProblem implements Problem<ISeq<Pair<Integer, Integer>>, EnumGene<Pair<Integer, Integer>>, Double> {

    private final ISeq<Pair<Integer, Integer>> _basicSet;
    private final Integer _size;

    /**
     * Constructor for the Metamorphic problem.
     * @param basicSet an initial sequence of transformer keys and seed pairs.
     * @param size The amount of transformers used.
     */
    public MetamorphicProblem(final ISeq<Pair<Integer, Integer>> basicSet, Integer size) {
        _basicSet = requireNonNull(basicSet);
        _size = size;
    }

    /**
     * Create a new metamorphic problem for the genetic algorithm.
     * @param maxTransformerValue the maximum value for the transformer keys.
     * @param numberOfTransformers the number of transformers used in each problem.
     * @param random a random number generator.
     * @return the new metamorphic problem.
     */
    public static MetamorphicProblem of(final int maxTransformerValue, final int numberOfTransformers, final RandomGenerator random) {
        return new MetamorphicProblem(random.ints(1, maxTransformerValue+1).limit(numberOfTransformers).mapToObj(i -> new ImmutablePair<>(Math.abs(i), random.nextInt())).collect(ISeq.toISeq()),
                numberOfTransformers);
    }

    /**
     * Create the transformer based on the key and seed specified.
     * @param key the transformer key.
     * @param seed the transformer seed.
     * @return a transformer that extends the BaseTransformer.
     */
    public static BaseTransformer createTransformers(Integer key, Integer seed) {
        switch (key) {
            case 1:
                return new IfTrueTransformer(seed);
            case 2:
                return new IfFalseElseTransformer(seed);
            case 3:
                return new RenameVariableTransformer(seed);
            default:
                return new EmptyMethodTransformer(seed);
        }
    }

    /**
     * This function run all transformers on the old dataset and then runs the next code2vec steps.
     * @param transformers the list of metamorphic transformers.
     * @return the fitness.
     */
    public static double runPipeline(List<BaseTransformer> transformers) {
        PipelineSupport.setCurrentDataset(PipelineSupport.runTransformations(transformers, PipelineSupport.getCurrentDataset()));
        PipelineSupport.runCode2vec(PipelineSupport.getCurrentDataset());
        return calculateFitness(calculateMetric());
    }

    /**
     * Calculate the scores for each metric.
     * @return a list of scores.
     */
    private static List<Double> calculateMetric() {
        List<Metric> metrics = new ArrayList<>();
        for(MetricCategory metricCategory: MetricCategory.values()) {
            metrics.add(createMetric(metricCategory.name()));
        }

        List<Double> scores = new ArrayList<>();
        for(Metric metric: metrics) {
            scores.add(metric.CalculateScore());
        }
        return scores;
    }

    /**
     * Create a new metric from the specified name.
     * @param name the metric name.
     * @return The new metric.
     */
    private static Metric createMetric(String name) {
        switch (name) {
            case "MRR":
                return new MRR();
            case "F1_score":
                return new F1_score();
            case "Percentage_MRR":
                return new Percentage_MRR();
            default:
                throw new IllegalArgumentException("Metric name not a correct metric.");
        }
    }

    /**
     * Calculate the global fitness of the metrics with the weights for each metric.
     * @param metrics the list of metrics.
     * @return The global fitness.
     */
    private static double calculateFitness(List<Double> metrics) {
        List<Double> weights = PipelineSupport.getWeights();
        double output = 0;
        for(int i = 0; i < metrics.size(); i++) {
            output += metrics.get(i)*weights.get(i);
        }
        return output;
    }


    /**
     * This overrides the Jenetics fitness function.
     * @return A function from the input sequence to the fitness.
     */
    @Override
    public Function<ISeq<Pair<Integer, Integer>>, Double> fitness() {
        return subset
                -> runPipeline(subset.stream().map(p -> createTransformers(p.getLeft(), p.getRight())).toList());
    }

    /**
     * This overrides the Jenetics Codec function.
     * @return A Codec from the input sequence to the gene.
     */
    @Override
    public Codec<ISeq<Pair<Integer, Integer>>, EnumGene<Pair<Integer, Integer>>> codec() {
        return Codecs.ofSubSet(_basicSet, _size);
    }
}
