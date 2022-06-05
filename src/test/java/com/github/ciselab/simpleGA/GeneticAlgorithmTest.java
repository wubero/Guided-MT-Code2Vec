package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.support.FileManagement;
import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GeneticAlgorithmTest {

    private MetricCache cache = new MetricCache();
    private GenotypeSupport genotypeSupport = new GenotypeSupport(cache);
    private GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(genotypeSupport);

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void initializeParametersTest() {
        String real = geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10,
                new SplittableRandom(101010));
        String expected = "{uniform rate: 0.700000, mutation rate: 0.0100000, tournament size: 3, elitism: true, increase rate: 0.400000, max transformer value: 6, max gene length: 10}";
        assertEquals(expected, real);
    }

    @Test
    public void evolvePopulationTest() {
        geneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 6, 10,
                new SplittableRandom(101010));
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
