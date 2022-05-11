package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

public class Transformations extends Metric {

    public Transformations() {
        super("Number_of_transformations");
    }

    @Override
    public double CalculateScore() {
        return 0;
    }
}
