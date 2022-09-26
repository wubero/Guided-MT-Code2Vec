package com.github.ciselab.support;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MetricCacheTest {

    @AfterEach
    public void after() {
        var config = new Configuration();
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

    @Tag("File")
    @Test
    public void initWeightsTest_allMetricsActive() throws IOException {
        String path = "src/test/resources/config_examples/config.properties";

        MetricCache cache = ConfigManagement.initializeMetricCache(path);

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
    public void initWeightsTest_negativeMetric_Works() throws IOException {
        String path = "src/test/resources/config_examples/negativeMetric.properties";

        MetricCache cache = ConfigManagement.initializeMetricCache(path);

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
    public void initWeightsTest_twoNegativeMetric_Works() throws IOException {
        String path = "src/test/resources/config_examples/twoNegativeMetrics.properties";

        MetricCache cache = ConfigManagement.initializeMetricCache(path);
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
    public void initWeightsTest_mixedMetrics_Works() throws IOException {
        String path = "src/test/resources/config_examples/mixedMetrics.properties";

        MetricCache cache = ConfigManagement.initializeMetricCache(path);

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
    public void initWeightsTest_mixedMetrics2_OneNegativeOnePositive() throws IOException {
        String path = "src/test/resources/config_examples/mixedMetrics2.properties";

        MetricCache cache = ConfigManagement.initializeMetricCache(path);

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
