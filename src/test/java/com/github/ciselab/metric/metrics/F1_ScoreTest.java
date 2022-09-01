package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class F1_ScoreTest {

    @Tag("File")
    @Test
    public void checkNameTest() {
        F1_score metric = new F1_score(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals("F1Score", metric.getName());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest() {
        F1_score metric = new F1_score(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals(0.61, metric.calculateScore());
    }
}
