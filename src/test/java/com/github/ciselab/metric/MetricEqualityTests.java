package com.github.ciselab.metric;

import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * This TestClass Covers extra tests for Equality and HashCodes of Different Metrics
 * it is separate as otherwise one MetricTestClass would need to know more than one Metric.'
 *
 * Identity Tests are in the Metrics Themselves!
 */
public class MetricEqualityTests {

    @Test
    public void testEquals_TwoDifferentMetrics_shouldBeNotEquals(){
        Metric a = new F1();
        Metric b = new EditDistance();

        assertNotEquals(a,b);
    }

    @Test
    public void testHashCode_TwoDifferentMetrics_shouldBeNotEquals(){
        Metric a = new F1();
        Metric b = new EditDistance();

        assertNotEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testEquals_TwoDifferentMetrics_shouldBeNotEquals_optionB(){
        Metric a = new Recall();
        Metric b = new Precision();

        assertNotEquals(a,b);
    }

    @Test
    public void testHashCode_TwoDifferentMetrics_shouldBeNotEquals_optionB(){
        Metric a = new Recall();
        Metric b = new Precision();

        assertNotEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testEquals_TwoDifferentMetrics_shouldBeNotEquals_optionC(){
        Metric a = new Transformations();
        Metric b = new PredictionLength();

        assertNotEquals(a,b);
    }

    @Test
    public void testHashCode_TwoDifferentMetrics_shouldBeNotEquals_optionC(){
        Metric a = new Transformations();
        Metric b = new PredictionLength();

        assertNotEquals(a.hashCode(),b.hashCode());
    }


    @Test
    public void testEquals_TwoDifferentMetrics_shouldBeNotEquals_optionD(){
        Metric a = new F1();
        Metric b = new PredictionLength();

        assertNotEquals(a,b);
    }

    @Test
    public void testHashCode_TwoDifferentMetrics_shouldBeNotEquals_optionD(){
        Metric a = new F1();
        Metric b = new PredictionLength();

        assertNotEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testEquals_TwoDifferentMetrics_shouldBeNotEquals_optionE(){
        Metric a = new Transformations();
        Metric b = new Recall();

        assertNotEquals(a,b);
    }

    @Test
    public void testHashCode_TwoDifferentMetrics_shouldBeNotEquals_optionE(){
        Metric a = new Transformations();
        Metric b = new Recall();

        assertNotEquals(a.hashCode(),b.hashCode());
    }
}
