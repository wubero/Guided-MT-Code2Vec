package com.github.ciselab.support;

import static org.junit.jupiter.api.Assertions.*;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetricCacheTest {

    GenotypeSupport genotypeSupport;
    MetricCache metricCache;

    @BeforeEach
    public void setUp() {
        metricCache = new MetricCache();
        genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void putFilesTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        metricCache.putFileCombination(transformers, "testFile");
        assertEquals(metricCache.getDir(transformers).get(), "testFile");
    }

    @Test
    public void fillFitnessTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        metricCache.fillFitness(transformers, arr);
        assertTrue(metricCache.getMetricResult(transformers).isPresent());
        assertSame(metricCache.getMetricResult(transformers).get(), arr);
        transformers.add(new RandomParameterNameTransformer());
        double[] second = new double[]{9,4,5};
        metricCache.fillFitness(transformers, second);
        assertTrue(metricCache.getMetricResult(transformers).isPresent());
        assertSame(metricCache.getMetricResult(transformers).get(), second);
    }

    @Test
    public void storeFilesTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        metricCache.storeFiles(transformers, "file", arr);
        assertTrue(metricCache.getDir(transformers).isPresent());
        assertEquals(metricCache.getDir(transformers).get(), "file");
        assertTrue(metricCache.getMetricResult(transformers).isPresent());
        assertSame(metricCache.getMetricResult(transformers).get(), arr);
    }

    @Test
    public void initWeightsTest_allMetricsActive() {
        metricCache.initWeights(true);
        assertTrue(metricCache.getActiveMetrics() > 1);
        for(boolean i: metricCache.getObjectives()) {
            assertTrue(i);
        }
        float sum = 0;
        for(float i: metricCache.getWeights()) {
            assertTrue(i < 1);
            sum += i;
        }
        assertFalse(sum > 1);
    }

    @Test
    public void getVarianceTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        List<BaseTransformer> transformers2 = new ArrayList<>();
        transformers2.add(new IfTrueTransformer());
        double[] arr2 = new double[]{5,4};
        metricCache.setActiveMetrics(2);
        metricCache.fillFitness(transformers, arr);
        metricCache.fillFitness(transformers2, arr2);
        Pair<double[], double[]> variance = metricCache.getStatistics();
        assertArrayEquals(new double[]{7.5, 3.5}, variance.getLeft());
        assertArrayEquals(new double[]{2.5, 0.5}, variance.getRight());
    }
}
