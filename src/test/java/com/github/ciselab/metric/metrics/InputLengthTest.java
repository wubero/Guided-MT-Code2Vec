package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class InputLengthTest {

    @Tag("File")
    @Test
    public void checkNameTest() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        assertEquals("InputLength", metric.getName());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        metric.setDataSet("input_test");
        assertEquals(102, metric.calculateScore());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest_withEmptyDir() {
        InputLength metric = new InputLength(GenotypeSupport.dir_path + "/src/test/resources/");
        metric.setDataSet("code_files");
        assertEquals(0, metric.calculateScore());
    }
}
