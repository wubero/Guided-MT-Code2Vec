package com.github.ciselab.algorithms;

import com.github.ciselab.lampion.guided.algorithms.GeneticAlgorithm;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;


public class GeneticAlgorithmTest {

    @Test
    public void initializeParametersTest() {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),new SplittableRandom(101010));

    }


    /*
    TODO: Reimplement
    @Test
    public void evolvePopulationTest() {
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(genotypeSupport,new ParetoFront(cache));

        geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10, new SplittableRandom(101010));

        RandomGenerator randomGenerator = new SplittableRandom(101010);
        geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10,
                randomGenerator);
        int popSize = 5;

        MetamorphicPopulation pop = new MetamorphicPopulation(popSize,
                randomGenerator, 6, false, genotypeSupport);
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual temp = new MetamorphicIndividual(genotypeSupport);
            temp.populateIndividual(randomGenerator, 3, 6);
            temp.setFitness(Math.random());
            pop.saveIndividual(i, temp);
        }
        MetamorphicPopulation newPop = geneticAlgorithm.evolvePopulation(pop);
        assertNotEquals(pop, newPop);
    }
    */

}
