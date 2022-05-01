package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.jenetics.prngine.LCG64ShiftRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

public class MetamorphicPopulationTest {

    RandomGenerator r = new LCG64ShiftRandom(101010);

    @Test
    public void createPopulation_getFittestTest() {
        int popSize = 3;
        MetamorphicPopulation population = new MetamorphicPopulation(popSize, r, 6, false);
        double bestFitness = -1;
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual individual = new MetamorphicIndividual();
            individual.createIndividual(r, 2, 6);
            double fitness = Math.random();
            if(fitness > bestFitness) {
                bestFitness = fitness;
            }
            individual.setFitness(fitness);
            population.saveIndividual(i, individual);
        }
        assertEquals(population.getFittest().getFitness(), bestFitness);
    }

    @Test
    public void createPopulation_withInitializeTest() {
        MetamorphicPopulation population = new MetamorphicPopulation(3, r, 6, true);
        assertEquals(population.size(), 3);
        for(int i = 0; i < population.size(); i++) {
            assertNotNull(population.getIndividual(i));
        }
    }

}
