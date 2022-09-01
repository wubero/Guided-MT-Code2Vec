package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.support.FileManagement;
import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

import com.github.ciselab.support.Pareto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GeneticAlgorithmTest {

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void initializeParametersTest() {
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(genotypeSupport,new Pareto(cache));

        String real = geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10, new SplittableRandom(101010));
        String expected = "{uniform rate: 0.7000, mutation rate: 0.0100, tournament size: 3, elitism: true, increase rate: 0.4000, max transformer value: 6, max gene length: 10}";
        assertEquals(expected, real);
    }

    @Test
    public void evolvePopulationTest() {
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(genotypeSupport,new Pareto(cache));

        geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10, new SplittableRandom(101010));
        int popSize = 5;
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(popSize,
                new SplittableRandom(101010), 6, false, genotypeSupport);
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual temp = new MetamorphicIndividual(genotypeSupport);
            temp.createIndividual(r, 3, 6);
            temp.setFitness(Math.random());
            pop.saveIndividual(i, temp);
        }
        MetamorphicPopulation newPop = geneticAlgorithm.evolvePopulation(pop);
        assertNotEquals(pop, newPop);
    }

}
