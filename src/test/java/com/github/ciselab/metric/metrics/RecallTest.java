package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Test;

public class RecallTest {

    @Test
    public void checkNameTest() {
        Recall metric = new Recall(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals("Recall", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        Recall metric = new Recall(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals(0.54, metric.calculateScore());
    }
}
