package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Test;

public class MRRTest {

    @Test
    public void checkNameTest() {
        MRR metric = new MRR(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals("MRR", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        MRR metric = new MRR(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals(0.75, metric.calculateScore());
    }
}
