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
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);

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
    public void testMetamorphicPopulation_withNegativeMetricsCached_getFittest_ShouldReturnHighestFitness_WhenMinimizing(){
        Random r = new Random(5);
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);

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
        assertEquals(0.75,result.get().getFitness(),0.001);
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
