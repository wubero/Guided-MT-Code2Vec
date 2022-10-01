package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (o instanceof Transformations ed) {
            return ed.getWeight() == this.getWeight();
        }
        return false;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(name).append(weight).hashCode();
    }
}
