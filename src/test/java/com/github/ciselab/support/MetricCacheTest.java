package com.github.ciselab.support;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.support.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MetricCacheTest {

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    /*
    TODO: Reimplement
    @Test
    public void putFilesTest() {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config.properties");
        config.initializeFields();

        List<BaseTransformer> transformers = new ArrayList<>();
        cache.putFileCombination(transformers, "testFile");
        assertEquals(cache.getDir(transformers).get(), "testFile");
    }

    @Test
    public void storeFilesTest() {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config.properties");
        config.initializeFields();

        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        cache.storeFiles(transformers, "file", arr);

        assertTrue(cache.getDir(transformers).isPresent());
        assertEquals(cache.getDir(transformers).get(), "file");
        assertTrue(cache.getMetricResult(transformers).isPresent());
        assertSame(cache.getMetricResult(transformers).get(), arr);
    }
*/
    @Tag("File")
    @Test
    public void initWeightsTest_allMetricsActive() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config_examples/config.properties");
        config.initializeFields();

        cache.initWeights();

        float sum = 0;
        for(double i: cache.getWeights()) {
            assertTrue(i <= 1.0 && i >= -1);
            sum += abs(i);
        }
        assertTrue(sum != 0);
        assertEquals(1.0,sum,0.01);
    }

    @Tag("File")
    @Test
    public void initWeightsTest_negativeMetric_Works() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config_examples/negativeMetric.properties");
        config.initializeFields();

        cache.initWeights();

        float sum = 0;
        for(double i: cache.getWeights()) {
            assertTrue(i <= 1.0 && i >= -1);
            sum += abs(i);
        }
        assertTrue(sum != 0);
        assertEquals(1.0,sum,0.01);
    }

    @Tag("File")
    @Test
    public void initWeightsTest_twoNegativeMetric_Works() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config_examples/twoNegativeMetrics.properties");
        config.initializeFields();

        cache.initWeights();

        float sum = 0;
        for(double i: cache.getWeights()) {
            assertTrue(i <= 1.0 && i >= -1);
            sum += abs(i);
        }
        assertTrue(sum != 0);
        assertEquals(1.0,sum,0.01);
    }

    @Tag("File")
    @Test
    public void initWeightsTest_mixedMetrics_Works() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config_examples/mixedMetrics.properties");
        config.initializeFields();

        cache.initWeights();

        float sum = 0;
        for(double i: cache.getWeights()) {
            assertTrue(i <= 1.0 && i >= -1);
            sum += abs(i);
        }
        assertTrue(sum != 0);
        assertEquals(1.0,sum,0.01);
    }


    @Tag("File")
    @Test
    public void initWeightsTest_mixedMetrics2_OneNegativeOnePositive() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config_examples/mixedMetrics2.properties");
        config.initializeFields();

        cache.initWeights();

        var metrics = cache.getActiveMetrics();
        for (var m : metrics) {
            switch (m.getName()){
                case "F1" : assertEquals(-0.5,m.getWeight(),0.01); break;
                case "MRR" : assertEquals(0.5,m.getWeight(),0.01); break;
                default: fail();
            }
        }
    }
}
