package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.helpers.StubMetric;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicPopulation;
import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;

import java.util.HashMap;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MetamorphicPopulationTest {

    @AfterEach
    public void after() {
        var config = new Configuration();
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

    @Test
    public void createPopulation_withInitializeTest() {
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicPopulation population = new MetamorphicPopulation(3, r,  true, support, 0);
        assertEquals(population.size(), 3);
        for(int i = 0; i < population.size(); i++) {
            assertNotNull(population.getIndividual(i));
        }
    }

    @Test
    public void populationStringTest() {
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicPopulation population = new MetamorphicPopulation(3, r, true, support, 0);
        assertTrue(population.toString().contains("MetamorphicPopulation{"));
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testMetamorphicPopulation_withMetricsCached_getFittest_ShouldReturnHighestFitness_WhenMinimizing(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,3);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,3);

        StubMetric stub = new StubMetric();
        stub.setWeight(1);
        stub.valuesToReturn.put(a,0.75);
        stub.valuesToReturn.put(b,0.50);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,0.75);
        cache.putMetricResults(a,aMetrics);

        HashMap<Metric,Double> bMetrics = new HashMap<>();
        bMetrics.put(stub,0.5);
        cache.putMetricResults(b,bMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);
        testObject.saveIndividual(b);

        var result = testObject.getFittest();
        assertTrue(result.isPresent());
        assertEquals(a,result.get());
        assertEquals(0.75,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testMetamorphicPopulation_withMetricsCached_getFittest_ShouldReturnHighestFitness_WhenMinimizing_VariantB(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,3);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,3);

        StubMetric stub = new StubMetric();
        stub.setWeight(1);
        stub.valuesToReturn.put(a,0.9);
        stub.valuesToReturn.put(b,0.2);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,0.9);
        cache.putMetricResults(a,aMetrics);

        HashMap<Metric,Double> bMetrics = new HashMap<>();
        bMetrics.put(stub,0.2);
        cache.putMetricResults(b,bMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);
        testObject.saveIndividual(b);

        var result = testObject.getFittest();
        assertTrue(result.isPresent());
        assertEquals(a,result.get());
        assertEquals(0.9,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testMetamorphicPopulation_withNegativeMetricsCached_getFittest_ShouldReturnHighestFitness_WhenMaximizing(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,3);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,3);

        StubMetric stub = new StubMetric();
        stub.setWeight(-1);
        stub.valuesToReturn.put(a,0.75);
        stub.valuesToReturn.put(b,0.50);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,0.75);
        cache.putMetricResults(a,aMetrics);
        HashMap<Metric,Double> bMetrics = new HashMap<>();
        bMetrics.put(stub,0.5);
        cache.putMetricResults(b,bMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);
        testObject.saveIndividual(b);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(b,result.get());
        assertEquals(0.5,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testMetamorphicPopulation_withNegativeMetricsCached_getFittest_ShouldReturnHighestFitness_WhenMaximizing_VariantB(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,3);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,3);

        StubMetric stub = new StubMetric();
        stub.setWeight(-1);
        stub.valuesToReturn.put(a,0.7);
        stub.valuesToReturn.put(b,0.2);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,0.7);
        cache.putMetricResults(a,aMetrics);
        HashMap<Metric,Double> bMetrics = new HashMap<>();
        bMetrics.put(stub,0.2);
        cache.putMetricResults(b,bMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);
        testObject.saveIndividual(b);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(b,result.get());
        assertEquals(1-0.2,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @ParameterizedTest
    @ValueSource(doubles = {0.0,0.1, 0.25, 0.5, 0.9, 1.0})
    public void testGetFittest_OneElement_ShouldReturnSpecifiedFitness_WhenMinimizing(double fitness){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();
        stub.setWeight(1);
        stub.valuesToReturn.put(a,fitness);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,fitness);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(fitness,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @ParameterizedTest
    @ValueSource(doubles = {0.0,0.1, 0.25, 0.5,0.9,1.0})
    public void testGetFittest_OneElement_ShouldReturnSpecifiedFitness_WhenMaximizing(double fitness){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();
        stub.setWeight(-1);
        stub.valuesToReturn.put(a,fitness);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,fitness);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(1-fitness,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @ParameterizedTest
    @ValueSource(doubles = {0.0,0.1, 0.25, 0.5,0.9,1.0})
    public void testGetFittest_OneElement_WeightIsPoint5_ShouldReturnHalfOfSpecifiedFitness_WhenMaximizing(double fitness){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();
        stub.setWeight(-0.5);
        stub.valuesToReturn.put(a,fitness);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,fitness);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        Double expectedFitness = (1.0/2.0) - (fitness/2);

        assertEquals(expectedFitness,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @ParameterizedTest
    @ValueSource(doubles = {0.0,0.1, 0.25, 0.5,0.9,1.0})
    public void testGetFittest_OneElement_WeightIsPoint5_ShouldReturnHalfOfSpecifiedFitness_WhenMinimizing(double fitness){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub = new StubMetric();
        stub.setWeight(0.5);
        stub.valuesToReturn.put(a,fitness);
        cache.addMetric(stub);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub,fitness);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);


        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals((fitness)/2,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_WithMixedMetrics_FitnessIsEvenedOut(){
        /**
         * If we have Metric A with weight 0.5 and Value 1
         * and Metric B with weight -0.5 and 1
         * We expect an output of 0.5 (That is, it did max in one and least in other)
         */
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(0.5);
        stub1.valuesToReturn.put(a,1.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-0.5);
        stub2.valuesToReturn.put(a,1.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,1.0);
        aMetrics.put(stub2,1.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(0.5,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_WithMixedMetrics_FitnessIsMaxed(){
        /**
         * If we have Metric A with weight 0.5 and Value 1
         * and Metric B with weight -0.5 and 0
         * We expect an output of 1.0
         * (Both Metrics are in their co-respective best value)
         */
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(0.5);
        stub1.valuesToReturn.put(a,1.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-0.5);
        stub2.valuesToReturn.put(a,0.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,1.0);
        aMetrics.put(stub2,0.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(1.0,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_WithMixedMetrics_FitnessIsMaxed_VariantB(){
        /**
         * If we have Metric A with weight 0.5 and Value 1
         * and Metric B with weight -0.5 and 0
         * We expect an output of 1.0
         * (Both Metrics are in their co-respective best value)
         * Variant B: Metrics flipped, otherwise same values
         */
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-0.5);
        stub1.valuesToReturn.put(a,0.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(0.5);
        stub2.valuesToReturn.put(a,1.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,0.0);
        aMetrics.put(stub2,1.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(1.0,result.get().getFitness(),0.001);
    }


    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_PositiveAndNegativeMetricResults_AreEvenedOut(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(0.5);
        stub1.valuesToReturn.put(a,0.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(0.5);
        stub2.valuesToReturn.put(a,1.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,0.0);
        aMetrics.put(stub2,1.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(0.5,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_PositiveAndNegativeMetricResults_AreEvenedOut_VariantB(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-0.5);
        stub1.valuesToReturn.put(a,0.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-0.5);
        stub2.valuesToReturn.put(a,1.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,0.0);
        aMetrics.put(stub2,1.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(0.5,result.get().getFitness(),0.001);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_With3MixedMetrics_FitnessIsMaxed(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-0.333);
        stub1.valuesToReturn.put(a,0.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(0.333);
        stub2.valuesToReturn.put(a,1.0);
        cache.addMetric(stub2);

        StubMetric stub3 = new StubMetric();
        stub3.setWeight(0.333);
        stub3.valuesToReturn.put(a,1.0);
        cache.addMetric(stub3);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,0.0);
        aMetrics.put(stub2,1.0);
        aMetrics.put(stub3,1.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(1.0,result.get().getFitness(),0.01);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_With3MixedMetrics_FitnessIsMaxed_VariantB(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(0.333);
        stub1.valuesToReturn.put(a,1.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-0.333);
        stub2.valuesToReturn.put(a,0.0);
        cache.addMetric(stub2);

        StubMetric stub3 = new StubMetric();
        stub3.setWeight(-0.333);
        stub3.valuesToReturn.put(a,0.0);
        cache.addMetric(stub3);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,1.0);
        aMetrics.put(stub2,0.0);
        aMetrics.put(stub3,0.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(1.0,result.get().getFitness(),0.01);
    }

    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_With3MixedMetrics_OneIsPoor_FitnessEvened(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(0.333);
        stub1.valuesToReturn.put(a,0.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(-0.333);
        stub2.valuesToReturn.put(a,0.0);
        cache.addMetric(stub2);

        StubMetric stub3 = new StubMetric();
        stub3.setWeight(-0.333);
        stub3.valuesToReturn.put(a,0.0);
        cache.addMetric(stub3);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,0.0);
        aMetrics.put(stub2,0.0);
        aMetrics.put(stub3,0.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(0.6666,result.get().getFitness(),0.01);
    }


    @Tag("Regression")
    @Tag("Integration")
    @Test
    public void testGetFittest_WithMixedMetrics_WorstValues_FitnessIsMin(){
        /**
         * If we have Metric A with weight 0.5 and Value 1
         * and Metric B with weight -0.5 and 0
         * We expect an output of 1.0
         * (Both Metrics are in their co-respective best value)
         */
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        StubMetric stub1 = new StubMetric();
        stub1.setWeight(-0.5);
        stub1.valuesToReturn.put(a,1.0);
        cache.addMetric(stub1);

        StubMetric stub2 = new StubMetric();
        stub2.setWeight(0.5);
        stub2.valuesToReturn.put(a,0.0);
        cache.addMetric(stub2);

        HashMap<Metric,Double> aMetrics = new HashMap<>();
        aMetrics.put(stub1,1.0);
        aMetrics.put(stub2,0.0);
        cache.putMetricResults(a,aMetrics);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);
        testObject.saveIndividual(a);

        var result = testObject.getFittest();

        assertTrue(result.isPresent());
        assertEquals(0.0,result.get().getFitness(),0.001);
    }

    @Test
    public void testMetamorphicPopulation_savingIndividual_shouldHaveIndividual(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);

        testObject.saveIndividual(a);

        assertEquals(1,testObject.getIndividuals().size());
    }

    @Test
    public void testSize_savingIndividual_sizeIsPresetSize(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        MetamorphicPopulation testObject = new MetamorphicPopulation(2,r,false,support,0);

        testObject.saveIndividual(a);

        assertEquals(2,testObject.size());
    }

    /**
     * @return A Cache without any active metrics, will not call file system for any evaluation
     */
    private static MetricCache makeEmptyCache(){
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x -> true);
        cache.getActiveMetrics().removeIf(x -> true);
        return cache;
    }
}
