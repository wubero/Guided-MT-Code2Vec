package com.github.ciselab.helpers;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.Metric;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to perform Integration Tests around Individuals, Genetic Algorithm and Caching.
 * The attributes are all public to easily change them.
 */
public class StubMetric extends Metric {

    public boolean secondary = false;
    public boolean canBeBiggerThanOne = false;

    public Map<MetamorphicIndividual,Double> valuesToReturn = new HashMap<>();
    public double defaultReturnValue = 0;

    @Override
    public boolean isSecondary() {
        return secondary;
    }

    @Override
    public boolean canBeBiggerThanOne() {
        return canBeBiggerThanOne;
    }

    @Override
    public Double apply(MetamorphicIndividual individual) {
        return valuesToReturn.getOrDefault(individual,defaultReturnValue);
    }

    @Override
    public boolean equals(Object obj){
        return (this == obj);
    }
}
