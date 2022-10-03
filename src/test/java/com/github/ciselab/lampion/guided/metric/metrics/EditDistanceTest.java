package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.EditDistance;
import com.github.ciselab.lampion.guided.metric.metrics.Recall;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditDistanceTest {

    @Test
    public void testCanBeBiggerThanOne_shouldBe(){
        Metric metric = new EditDistance();
        assertTrue(metric.canBeBiggerThanOne());
    }

    @Test
    public void testIsSecondary_shouldNotBe(){
        Metric metric = new EditDistance();
        assertFalse(metric.isSecondary());
    }

    @Test
    public void checkNameTest() {
        EditDistance metric = new EditDistance();
        assertEquals("EDITDIST", metric.getName());
    }

    @Tag("File")
    @Test
    public void testEditDistance_PathIsOk_shouldGiveValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        EditDistance metric = new EditDistance();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/resources/metric_files");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertNotEquals(Double.NaN,result);
        assertTrue(result>=0);
    }

    @Test
    public void testEditDistance_PathIsBad_NaNValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        EditDistance metric = new EditDistance();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }

    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new EditDistance();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new EditDistance();
        Metric b = new EditDistance();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new EditDistance();
        a.setWeight(0.75);
        Metric b = new EditDistance();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new EditDistance();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new EditDistance();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new EditDistance();
        Metric b = new EditDistance();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new EditDistance();
        a.setWeight(0.75);
        Metric b = new EditDistance();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

}
