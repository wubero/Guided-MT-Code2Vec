package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

public class MRR extends Metric {

    public MRR(String metricName) {
        super(metricName);
    }

    @Override
    public double CalculateScore() {
        return 0;
    }
}
