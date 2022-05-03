package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.support.GenotypeSupport;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GeneticAlgorithmTest {

    @AfterEach
    public void after() {
        GenotypeSupport.removeOtherDirs();
    }

    @Test
    public void initializeParametersTest() {
        String real = GeneticAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 0.7, 6, 10, new SplittableRandom(101010));
        String expected = "{uniform rate: 0.700000, mutation rate: 0.0100000, tournament size: 3, elitism: true, increase rate: 0.400000, decrease rate: 0.700000, max transformer value: 6, max gene length: 10}";
        assertEquals(expected, real);
    }

    @Test
    public void evolvePopulationTest() {
        int popSize = 5;
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(popSize, new SplittableRandom(101010), 6, false);
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual temp = new MetamorphicIndividual();
            temp.createIndividual(r, 3, 6);
            temp.setFitness(Math.random());
            pop.saveIndividual(i, temp);
        }
        MetamorphicPopulation newPop = GeneticAlgorithm.evolvePopulation(pop);
        assertNotEquals(pop, newPop);
    }
}
