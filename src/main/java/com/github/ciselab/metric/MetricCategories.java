package com.github.ciselab.metric;

/**
 * Class for all metric categories used in the Guided-Mt-Code2Vec program.
 */
public class MetricCategories {
    /**
     * The metrics currently implemented for the Guided-MT-Code2Vec project.
     */
    public enum MetricCategory {
        MRR,
        F1Score,
        PercentageMRR,
        Precision,
        Recall,
        EditDistance
    }

    /**
     * Secondary metrics that are not used to calculate the fitness but used in the Pareto front.
     */
    public enum SecondaryMetrics {
        InputLength,
        PredictionLength,
        NumberOfTransformations
    }
}
