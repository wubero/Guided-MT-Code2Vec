package com.github.ciselab.lampion.guided.support;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.ciselab.lampion.guided.helpers.StubMetric;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    public void testDoMaximize_noNegativeMetrics_ShouldNotMaximize(){
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);
        StubMetric stub1 = new StubMetric();
        stub1.setWeight(1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        testObject.addMetric(stub1);
        testObject.addMetric(stub2);

        assertFalse(testObject.doMaximize());
    }

    @Test
    public void testDoMaximize_OnlyNegativeWeights_ShouldMaximize(){
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);
        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-1);

        testObject.addMetric(stub1);
        testObject.addMetric(stub2);

        assertTrue(testObject.doMaximize());

    }
    @Test
    public void testDoMaximize_MixedeMetrics_ShouldMaximize(){
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);
        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-1);
        StubMetric stub2 = new StubMetric();
        stub2.setWeight(1);

        testObject.addMetric(stub1);
        testObject.addMetric(stub2);

        assertTrue(testObject.doMaximize());
    }

    @Tag("Regression")
    @Test
    public void testGetMetricResults_IndividualWasStored_shouldHaveValues(){
        /*
        This is a sub-piece of an issue we had with Genetic Populations not returning fitness,
        despite things being stored in cache.
        To have a better overview, this Test was created to test sub-parts.
         */
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();
        stub.setWeight(1);
        stub.valuesToReturn.put(a,0.75);
        stub.valuesToReturn.put(b,0.50);
        testObject.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,0.75);
        testObject.putMetricResults(a,aMetrics);
        HashMap<Metric,Double> bMetrics = new HashMap<>();
        bMetrics.put(stub,0.5);
        testObject.putMetricResults(b,bMetrics);

        assertTrue(testObject.getMetricResults(a).isPresent());
        assertTrue(testObject.getMetricResults(b).isPresent());
    }

    @Tag("Regression")
    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.25, 0.5, 1.0})
    public void testGetMetricResults_IndividualWasStored_shouldReturnValueAsSpecified(Double returnValue){
        var config = new Configuration();
        MetricCache testObject = new MetricCache();
        testObject.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(testObject,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,returnValue);
        testObject.putMetricResults(a,aMetrics);

        assertEquals(returnValue,testObject.getMetricResults(a).get().get(stub),0.001);
    }

}
