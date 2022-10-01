package com.github.ciselab.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.InputLength;
import com.github.ciselab.lampion.guided.metric.metrics.PredictionLength;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.metric.metrics.PredictionLength;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PredictionLengthTest {

    @Test
    public void checkNameTest() {
        PredictionLength metric = new PredictionLength();
        assertEquals("PREDLENGTH", metric.getName());
    }

    @Test
    public void testCanBeBiggerThanOne_shouldBe(){
        Metric metric = new PredictionLength();
        assertTrue(metric.canBeBiggerThanOne());
    }
    @Test
    public void testIsSecondary_shouldBe(){
        Metric metric = new PredictionLength();
        assertTrue(metric.isSecondary());
    }

    @Tag("File")
    @Test
    public void testPredictionLength_PathIsOk_shouldGiveValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        PredictionLength metric = new PredictionLength();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/resources/metric_files");

        var result = metric.apply(testObject);
        assertNotNull(result);
        assertNotEquals(Double.NaN,result);
        assertTrue(result>=0 );
    }

    @Test
    public void testPredictionLength_PathIsBad_NaNValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        PredictionLength metric = new PredictionLength();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }


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
