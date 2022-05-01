package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.jenetics.prngine.LCG64ShiftRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

public class MetamorphicAlgorithmTest {

    @Test
    public void initializeParametersTest() {
        String real = MetamorphicAlgorithm.initializeParameters(0.7, 0.01, 3, true, 0.4, 0.7, 6, 10, new LCG64ShiftRandom(101010));
        String expected = "{uniform rate: 0.700000, mutation rate: 0.0100000, tournament size: 3, elitism: true, increase rate: 0.400000, decrease rate: 0.700000, max transformer value: 6, max gene length: 10}";
        assertEquals(expected, real);
    }

    @Test
    public void evolvePopulationTest() {
        int popSize = 5;
        RandomGenerator r = new LCG64ShiftRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(popSize, new LCG64ShiftRandom(101010), 6, false);
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual temp = new MetamorphicIndividual();
            temp.createIndividual(r, 3, 6);
            temp.setFitness(Math.random());
            pop.saveIndividual(i, temp);
        }
        MetamorphicPopulation newPop = MetamorphicAlgorithm.evolvePopulation(pop);
        assertNotEquals(pop, newPop);
    }
}
