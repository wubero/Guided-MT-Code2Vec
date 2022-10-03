package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.PercentageMRR;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.metric.metrics.PercentageMRR;
import com.github.ciselab.lampion.guided.metric.metrics.Recall;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Percentage_MRRTest {

    @Test
    public void testCanBeBiggerThanOne_shouldNotBe(){
        Metric metric = new PercentageMRR();
        assertFalse(metric.canBeBiggerThanOne());
    }

    @Test
    public void testIsSecondary_shouldNotBe(){
        Metric metric = new PercentageMRR();
        assertFalse(metric.isSecondary());
    }

    @Tag("File")
    @Test
    public void testPMRR_PathIsOk_shouldGiveValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        PercentageMRR metric = new PercentageMRR();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/resources/metric_files");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertNotEquals(Double.NaN,result);
        assertTrue(result>=0  && result <=1);
    }

    @Test
    public void testPMRR_PathIsBad_NaNValue(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        PercentageMRR metric = new PercentageMRR();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }

    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new PercentageMRR();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new PercentageMRR();
        Metric b = new PercentageMRR();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new PercentageMRR();
        a.setWeight(0.75);
        Metric b = new PercentageMRR();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new PercentageMRR();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new PercentageMRR();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new PercentageMRR();
        Metric b = new PercentageMRR();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new PercentageMRR();
        a.setWeight(0.75);
        Metric b = new PercentageMRR();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }
}
