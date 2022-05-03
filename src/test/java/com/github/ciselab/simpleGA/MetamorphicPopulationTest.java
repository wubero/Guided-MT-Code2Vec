package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.support.GenotypeSupport;
import io.jenetics.prngine.LCG64ShiftRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MetamorphicPopulationTest {

    RandomGenerator r = new LCG64ShiftRandom(101010);

    @AfterEach
    public void after() {
        GenotypeSupport.removeOtherDirs();
    }

    @Test
    public void createPopulation_getFittest_maximizeTest() {
        int popSize = 3;
        MetamorphicPopulation population = new MetamorphicPopulation(popSize, r, 6, false);
        double bestFitness = -1;
        GenotypeSupport.setMaximize(true);
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
    public void createPopulation_getFittest_minimizeTest() {
        int popSize = 3;
        MetamorphicPopulation population = new MetamorphicPopulation(popSize, r, 6, false);
        double bestFitness = 10;
        GenotypeSupport.setMaximize(false);
        for(int i = 0; i < popSize; i++) {
            MetamorphicIndividual individual = new MetamorphicIndividual();
            individual.createIndividual(r, 2, 6);
            double fitness = Math.random();
            if(fitness < bestFitness) {
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

    @Test
    public void populationStringTest() {
        MetamorphicPopulation population = new MetamorphicPopulation(3, r, 6, true);
        assertTrue(population.toString().contains("MetamorphicPopulation{"));
    }

}
