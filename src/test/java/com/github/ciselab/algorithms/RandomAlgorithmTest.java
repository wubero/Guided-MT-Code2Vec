package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    //TODO: Reimplement

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
}
