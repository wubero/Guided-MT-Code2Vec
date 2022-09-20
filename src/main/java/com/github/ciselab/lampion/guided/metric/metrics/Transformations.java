package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

public class Transformations extends Metric {

    public Transformations() {
        this.name = Name.TRANSFORMATIONS;
    }

    @Override
    public boolean isSecondary() {
        return true;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return (Double) (double) individual.getTransformers().size();
    }

    @Override
    public boolean canBeBiggerThanOne() {
        return true;
    }

}
