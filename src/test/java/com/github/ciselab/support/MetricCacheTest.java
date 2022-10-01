package com.github.ciselab.support;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.ciselab.helpers.StubMetric;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
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

        var weights = cache.getActiveMetrics().stream().map(m -> m.getWeight()).collect(Collectors.toList());

        float sum = 0;
        for(double i: weights) {
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
        var weights = cache.getActiveMetrics().stream()
                .map(m -> m.getWeight())
                .collect(Collectors.toList());


        float sum = 0;
        for(double i: weights) {
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
        var weights = cache.getActiveMetrics().stream()
                .map(m -> m.getWeight())
                .collect(Collectors.toList());

        float sum = 0;
        for(double i: weights) {
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
        var weights = cache.getActiveMetrics().stream()
                .map(m -> m.getWeight())
                .collect(Collectors.toList());

        float sum = 0;
        for(double i: weights) {
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

    @Test
    public void testConstructor_ShouldBeInitializedWithoutActiveMetrics(){
        MetricCache cache = new MetricCache();

        assertEquals(0,cache.getActiveMetrics().size());
    }

    @Test
    public void testConstructor_shouldHaveNonActiveMetrics_byDefault(){
        MetricCache cache = new MetricCache();

        assertNotEquals(0,cache.getMetrics());
    }

    @Test
    public void testEmptying_CanEmptyAGivenMetricCache_WillNotHaveMetricsAnymore(){
        MetricCache cache = new MetricCache();

        cache.getMetrics().removeIf(x ->true);

        assertEquals(0,cache.getActiveMetrics().size());
        assertEquals(0,cache.getMetrics().size());
    }

    @Test
    public void testAddingMetric_ToEmptyCache_MetricHasWeight_ShouldBeActive(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub = new StubMetric();
        stub.setWeight(1);

        cache.addMetric(stub);

        assertEquals(1,cache.getActiveMetrics().size());
        assertEquals(1,cache.getMetrics().size());
    }

    @Test
    public void testAddingMetric_ToEmptyCache_MetricHasNegativeWeight_ShouldBeActive(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub = new StubMetric();
        stub.setWeight(-1);

        cache.addMetric(stub);

        assertEquals(1,cache.getActiveMetrics().size());
        assertEquals(1,cache.getMetrics().size());
    }

    @Test
    public void testAddingMetric_ToEmptyCache_MetricHasNoWeight_ShouldNotBeActive(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub = new StubMetric();
        stub.setWeight(0);

        cache.addMetric(stub);

        assertEquals(0,cache.getActiveMetrics().size());
        assertEquals(1,cache.getMetrics().size());
    }

    @Test
    public void testAddingTwoMetrics_TwoStubMetricsAreKept(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        cache.addMetric(stub1);
        cache.addMetric(stub2);

        assertEquals(2,cache.getActiveMetrics().size());
        assertEquals(2,cache.getMetrics().size());
    }

    @Test
    public void testAddingTwoMetrics_TwoStubMetrics_withoutInitWeights_WeightsAreRaw(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        cache.addMetric(stub1);
        cache.addMetric(stub2);

        for (Metric f : cache.getActiveMetrics()){
            assertEquals(1,f.getWeight());
        }
    }

    @Test
    public void testAddingTwoMetrics_TwoStubMetrics_withInitWeights_WeightsAreEvenedOut(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        cache.addMetric(stub1);
        cache.addMetric(stub2);

        cache.initWeights();

        for (Metric f : cache.getActiveMetrics()){
            assertEquals(0.5,f.getWeight());
        }
    }

    @Test
    public void testStoreMetricResults_forOneMetric_shouldNotFail(){
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        Map<Metric,Double> results = new HashMap<>();
        results.put(stub1,0.5);

        testObject.storeMetricResults(a,results);
    }

    @Test
    public void testGetMetricResults_forOneMetric_shouldReturnNonEmptyOptional(){
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        Map<Metric,Double> metricResults = new HashMap<>();
        metricResults.put(stub1,0.5);

        testObject.storeMetricResults(a,metricResults);

        var results = testObject.getMetricResults(a);

        assertTrue(results.isPresent());
        assertEquals(1,results.get().size());
        assertEquals(0.5,results.get().get(stub1));
    }

    @Test
    public void testGetMetricResults_forTwoMetrics_shouldReturnNonEmptyOptional(){
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        Map<Metric,Double> metricResults = new HashMap<>();
        metricResults.put(stub1,0.3);
        metricResults.put(stub2,0.7);

        testObject.storeMetricResults(a,metricResults);

        var results = testObject.getMetricResults(a);

        assertTrue(results.isPresent());
        assertEquals(2,results.get().size());

        assertEquals(0.3,results.get().get(stub1));
        assertEquals(0.7,results.get().get(stub2));
    }

    @Test
    public void testGetMetricResults_IndividualWasNotStored_shouldBeEmpty(){
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        var results = testObject.getMetricResults(a);

        assertTrue(results.isEmpty());
    }
}
