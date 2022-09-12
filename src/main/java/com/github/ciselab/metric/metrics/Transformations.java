package com.github.ciselab.metric.metrics;

import com.github.ciselab.algorithms.MetamorphicIndividual;
import com.github.ciselab.metric.Metric;

public class Transformations extends Metric {

    public Transformations() {
        super("NumberOfTransformations");
    }

    @Override
    public boolean isSecondary() {
        return true;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return (Double) (double) individual.getTransformers().size();
    }
}
