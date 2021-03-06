package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TransformationsTest {

    @Test
    public void checkNameTest() {
        Transformations metric = new Transformations();
        assertEquals("NumberOfTransformations", metric.getName());
    }

    @Test
    public void calculateScoreTest() {
        Transformations metric = new Transformations();
        assertEquals(0, metric.calculateScore());
        metric.setLength(5);
        assertEquals(5, metric.calculateScore());
    }
}
