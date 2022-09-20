package com.github.ciselab.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.metrics.EditDistance;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
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

    @Tag("File")
    @Test
    public void testF1Score_PathIsOk_shouldGiveValue(){
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
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
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        F1 metric = new F1();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }
}
