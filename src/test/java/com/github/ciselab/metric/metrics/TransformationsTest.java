package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.Transformations;
import com.github.ciselab.lampion.guided.metric.metrics.Transformations;
import org.junit.jupiter.api.Test;

public class TransformationsTest {

    @Test
    public void checkNameTest() {
        Transformations metric = new Transformations();
        assertEquals("TRANSFORMATIONS", metric.getName());
    }

    /* TODO: Reimplement
    @Test
    public void calculateScoreTest() {
        Transformations metric = new Transformations();
        assertEquals(0, metric.calculateScore());
        metric.setLength(5);
        assertEquals(5, metric.calculateScore());
    }

     */

    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new Transformations();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new Transformations();
        Metric b = new Transformations();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new Transformations();
        a.setWeight(0.75);
        Metric b = new Transformations();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new Transformations();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new Transformations();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new Transformations();
        Metric b = new Transformations();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new Transformations();
        a.setWeight(0.75);
        Metric b = new Transformations();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }
}
