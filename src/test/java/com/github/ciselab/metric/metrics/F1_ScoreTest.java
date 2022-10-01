package com.github.ciselab.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.metric.metrics.Recall;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class F1_ScoreTest {

    @Test
    public void checkNameTest() {
        F1 metric = new F1();
        assertEquals("F1", metric.getName());
    }

    @Test
    public void testCanBeBiggerThanOne_shouldNotBe(){
        Metric metric = new F1();
        assertFalse(metric.canBeBiggerThanOne());
    }

    @Test
    public void testIsSecondary_shouldNotBe(){
        Metric metric = new F1();
        assertFalse(metric.isSecondary());
    }

    @Tag("File")
    @Test
    public void testF1Score_PathIsOk_shouldGiveValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        F1 metric = new F1();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/resources/metric_files");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertNotEquals(Double.NaN,result);
        assertTrue(result>=0  && result <=1);
    }

    @Test
    public void testF1_PathIsBad_NaNValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        F1 metric = new F1();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }


    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new F1();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new F1();
        Metric b = new F1();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new F1();
        a.setWeight(0.75);
        Metric b = new F1();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new F1();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new F1();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new F1();
        Metric b = new F1();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new F1();
        a.setWeight(0.75);
        Metric b = new F1();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }
}
