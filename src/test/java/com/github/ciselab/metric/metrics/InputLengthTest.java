package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.support.GenotypeSupport;
import org.junit.jupiter.api.Test;

public class InputLengthTest {

    @Test
    public void checkNameTest() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        assertEquals("InputLength", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        metric.setDataSet("input_test");
        assertEquals(49, metric.calculateScore());
    }

    @Test
    public void calculateScoreTest_withEmptyDir() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        metric.setDataSet("code_files");
        assertEquals(0, metric.calculateScore());
    }
}
