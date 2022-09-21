package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicPopulation;
import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetamorphicPopulationTest {

    RandomGenerator r = new SplittableRandom(101010);
    GenotypeSupport genotypeSupport;
    ConfigManagement configManagement;

    @BeforeEach
    public void setUp() {
        genotypeSupport = new GenotypeSupport(new MetricCache());
        configManagement = genotypeSupport.getConfigManagement();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void createPopulation_withInitializeTest() {
        MetamorphicPopulation population = new MetamorphicPopulation(3, r, 6, true, genotypeSupport, 0);
        assertEquals(population.size(), 3);
        for(int i = 0; i < population.size(); i++) {
            assertNotNull(population.getIndividual(i));
        }
    }

    @Test
    public void populationStringTest() {
        MetamorphicPopulation population = new MetamorphicPopulation(3, r, 6, true, genotypeSupport, 0);
        assertTrue(population.toString().contains("MetamorphicPopulation{"));
    }

}
