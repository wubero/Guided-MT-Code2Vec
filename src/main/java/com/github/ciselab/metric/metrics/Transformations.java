package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

public class Transformations extends Metric {

    private int length;

    public Transformations() {
        super("NumberOfTransformations");
        length = 0;
    }

    public void setLength(int l) {
        length = l;
    }

    @Override
    public double calculateScore() {
        return length;
    }
}
