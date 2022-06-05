package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Test;

public class PredictionLengthTest {

    @Test
    public void checkNameTest() {
        PredictionLength metric = new PredictionLength(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithoutScore.txt");
        assertEquals("PredictionLength", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        PredictionLength metric = new PredictionLength(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithoutScore.txt");
        assertEquals(7.75, metric.calculateScore());
    }
}
