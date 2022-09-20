package com.github.ciselab.metric.metrics;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.metric.metrics.MRR;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MRRTest {

    @Tag("File")
    @Test
    public void testMRRScore_PathIsOk_shouldGiveValue(){
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        MRR metric = new MRR();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/resources/metric_files");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertNotEquals(Double.NaN,result);
        assertTrue(result>=0  && result <=1);
    }

    @Test
    public void testMRR_PathIsBad_NaNValue(){
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        MRR metric = new MRR();

        MetamorphicIndividual testObject = new MetamorphicIndividual(support, 0);
        testObject.setResultPath("./src/test/bad_path");

        var result = metric.apply(testObject);

        assertNotNull(result);
        assertEquals(Double.NaN,result);
    }
}
