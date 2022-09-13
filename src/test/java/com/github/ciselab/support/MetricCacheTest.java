package com.github.ciselab.support;

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
import org.junit.jupiter.api.Test;

public class MetricCacheTest {

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config.properties");
        config.initializeFields();
        
    }

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
    @Test
    public void initWeightsTest_allMetricsActive() throws FileNotFoundException {
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache);
        ConfigManagement config = support.getConfigManagement();
        config.setConfigFile("src/test/resources/config.properties");
        config.initializeFields();

        cache.initWeights(true);

        float sum = 0;
        for(double i: cache.getWeights()) {
            assertTrue(i <= 1.0);
            sum += i;
        }
        assertFalse(sum > 1);
    }

}
