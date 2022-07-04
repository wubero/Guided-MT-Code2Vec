package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class RecallTest {

    @Tag("File")
    @Test
    public void checkNameTest() {
        Recall metric = new Recall(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals("Recall", metric.getName());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest() {
        Recall metric = new Recall(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals(0.54, metric.calculateScore());
    }
}
