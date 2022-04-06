package com.github.ciselab.ga;

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
import com.github.ciselab.program.MainPipeline;
import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.internal.compiler.batch.Main;

public class MetamorphicProblem implements Problem<ISeq<Pair<Integer, Integer>>, EnumGene<Pair<Integer, Integer>>, Double> {

    private static final List<String> transformerOptions =
            new ArrayList<>(Arrays.asList("IfTrueTransformer", "IfFalseElseTransformer", "RenameVariableTransformer"));
    private static String currentInput = "java-small_0";

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

    public static double runTransformations(List<BaseTransformer> transformers) {
        currentInput = MainPipeline.runTransformations(transformers, currentInput);
        MainPipeline.runCode2vec(currentInput);
        List<Double> metricScores = calculateMetric();
        return calculateFitness(metricScores);
    }

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

    private static double calculateFitness(List<Double> metrics) {
        List<Double> weights = MainPipeline.getWeights();
        double output = 0;
        for(int i = 0; i < metrics.size(); i++) {
            output += metrics.get(i)*weights.get(i);
        }
        return output;
    }


    @Override
    public Function<ISeq<Pair<Integer, Integer>>, Double> fitness() {
        return subset
                -> runTransformations(subset.stream().map(p -> createTransformers(p.getLeft(), p.getRight())).toList());
    }

    @Override
    public Codec<ISeq<Pair<Integer, Integer>>, EnumGene<Pair<Integer, Integer>>> codec() {
        return null;
    }
}
