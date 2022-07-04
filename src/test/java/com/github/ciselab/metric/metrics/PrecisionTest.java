package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PrecisionTest {

    @Tag("File")
    @Test
    public void checkNameTest() {
        Precision metric = new Precision(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals("Precision", metric.getName());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest() {
        Precision metric = new Precision(GenotypeSupport.dir_path + "/src/test/resources/F1Test.txt");
        assertEquals(0.69, metric.calculateScore());
    }
}
