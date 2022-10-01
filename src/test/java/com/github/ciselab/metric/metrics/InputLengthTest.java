package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.PredictionLength;
import com.github.ciselab.lampion.guided.metric.metrics.InputLength;
import org.junit.jupiter.api.Test;

public class InputLengthTest {

    @Test
    public void checkNameTest() {
        InputLength metric = new InputLength();
        assertEquals("INPUTLENGTH", metric.getName());
    }

    //TODO: Reimplement

    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new PredictionLength();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new PredictionLength();
        Metric b = new PredictionLength();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new PredictionLength();
        a.setWeight(0.75);
        Metric b = new PredictionLength();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new PredictionLength();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new PredictionLength();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new PredictionLength();
        Metric b = new PredictionLength();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new PredictionLength();
        a.setWeight(0.75);
        Metric b = new PredictionLength();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }
}
