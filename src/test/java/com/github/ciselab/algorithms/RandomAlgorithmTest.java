package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicPopulation;
import com.github.ciselab.lampion.guided.algorithms.RandomAlgorithm;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class RandomAlgorithmTest {

    private GenotypeSupport genotypeSupport;
    private RandomAlgorithm randomAlgorithm;

    @BeforeEach
    public void setUp(){
        MetricCache cache = new MetricCache();
        genotypeSupport = new GenotypeSupport(cache);
        randomAlgorithm = new RandomAlgorithm(genotypeSupport, new ParetoFront(cache));
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void InitializeParametersTest() {
        RandomGenerator randomGenerator = new SplittableRandom(101010);
        String init = randomAlgorithm.initializeParameters(6, randomGenerator);
        String expected = "{max transformer value: 6}";
        assertEquals(expected, init);
    }

    @Test
    public void nextGenerationTest() {
        RandomGenerator randomGenerator = new SplittableRandom(101010);
        randomAlgorithm.initializeParameters(6, randomGenerator);
        MetamorphicPopulation myPop = new MetamorphicPopulation(3, randomGenerator, 6, false, genotypeSupport);
        for(int i = 0; i < 3; i++) {
            MetamorphicIndividual newIndiv = new MetamorphicIndividual(genotypeSupport);
            newIndiv.populateIndividual(randomGenerator, 1, 6);
            myPop.saveIndividual(i, newIndiv);
        }
        assertEquals(1, myPop.getAverageSize());
        MetamorphicPopulation newPop = randomAlgorithm.nextGeneration(myPop);
        assertNotEquals(myPop.getAverageSize(), newPop.getAverageSize());
        assertEquals(2, newPop.getAverageSize());
    }
}
