package com.github.ciselab.lampion.guided.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.github.ciselab.lampion.guided.algorithms.RandomAlgorithm;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class RandomAlgorithmTest {


    @AfterEach
    public void after() {

        var config = new Configuration();
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

    @Test
    public void InitializeParametersTest() {
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        var randomAlgorithm = new RandomAlgorithm(support, new ParetoFront(cache));

        RandomGenerator randomGenerator = new SplittableRandom(101010);
        String init = randomAlgorithm.initializeParameters(6, randomGenerator);
        String expected = "{max transformer value: 6}";
        assertEquals(expected, init);
    }
}
