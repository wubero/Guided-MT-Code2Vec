package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Test;

public class Percentage_MRRTest {

    @Test
    public void checkNameTest() {
        Percentage_MRR metric = new Percentage_MRR(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals("PercentageMRR", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        Percentage_MRR metric = new Percentage_MRR(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals(0.2525, metric.calculateScore());
    }
}
