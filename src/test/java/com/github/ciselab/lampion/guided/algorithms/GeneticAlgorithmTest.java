package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.guided.algorithms.GeneticAlgorithm;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.SplittableRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


public class GeneticAlgorithmTest {

    @Test
    public void initializeParametersTest() {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),new SplittableRandom(101010));
    }


    @Tag("Probabilistic")
    @Tag("Seeded")
    @ParameterizedTest
    @ValueSource(ints = {3,5,10})
    public  void testMutate_withHighGrowthRate_ShouldHaveOnAverageHighElements(int growthFactor){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(growthFactor);
        config.genetic.setIncreaseSizeRate(1);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var averageIncrease = IntStream.range(0,100)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).average();

        assertTrue(averageIncrease.isPresent());
        assertTrue(averageIncrease.getAsDouble()>1.5);

        assertEquals(growthFactor,averageIncrease.getAsDouble(),growthFactor);
    }

    @Tag("Probabilistic")
    @Tag("Seeded")
    @ParameterizedTest
    @ValueSource(ints = {20,40,60,100})
    public  void testMutate_withVeryHighGrowthRate_ShouldHaveOnAverageHighElements(int growthFactor){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(growthFactor);
        config.genetic.setIncreaseSizeRate(1);
        config.genetic.setMaxGeneLength(200);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var averageIncrease = IntStream.range(0,200)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).average();

        assertTrue(averageIncrease.isPresent());
        assertTrue(averageIncrease.getAsDouble()>1.5);

        assertEquals(growthFactor,averageIncrease.getAsDouble(),growthFactor);
    }


    @Tag("Probabilistic")
    @Tag("Seeded")
    @ParameterizedTest
    @ValueSource(ints = {20,40,60,100})
    public  void testMutate_withVeryHighGrowthRate_butMaxLength10_ShouldHaveOnAverage10Elements(int growthFactor){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(growthFactor);
        config.genetic.setIncreaseSizeRate(1);
        config.genetic.setMaxGeneLength(10);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var anyAboveMaxSize = IntStream.range(0,250)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).anyMatch(x -> x > 10);

        assertFalse(anyAboveMaxSize);
    }


    @Tag("Probabilistic")
    @Tag("Seeded")
    @RepeatedTest(3)
    public  void testMutate_withHighGrowthRate_ShouldBeHigherThanOneMutation(){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(3);
        config.genetic.setIncreaseSizeRate(1);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var averageIncrease = IntStream.range(0,250)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).average();

        assertTrue(averageIncrease.isPresent());
        assertTrue(averageIncrease.getAsDouble()>1.5);
    }


    @Tag("Probabilistic")
    @Tag("Seeded")
    @Tag("Regression")
    @ParameterizedTest
    @ValueSource(ints = {3,5,10})
    public  void testMutate_HasElementsSmallerThanGrowthRate(int growthFactor){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(growthFactor);
        config.genetic.setIncreaseSizeRate(1);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var anyAboveGrowthRate = IntStream.range(0,250)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).anyMatch(x -> x < growthFactor);

        assertTrue(anyAboveGrowthRate);
    }
    @Tag("Probabilistic")
    @Tag("Seeded")
    @Tag("Regression")
    @ParameterizedTest
    @ValueSource(ints = {3,5,7,9,10,12})
    public  void testMutate_HasElementsBiggerThanGrowthRate(int growthFactor){
        var r = new SplittableRandom(10);
        var config = new Configuration();
        config.genetic.setGrowthFactor(growthFactor);
        config.genetic.setIncreaseSizeRate(1);
        MetricCache cache = new MetricCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        GeneticAlgorithm ga = new GeneticAlgorithm(config.genetic,cache,support,new ParetoFront(cache),r);

        var anyAboveGrowthRate = IntStream.range(0,2000)
                .map(t -> {
                    var mi = new MetamorphicIndividual(support,0);
                    ga.mutate(mi);
                    return mi.getLength();
                }).anyMatch(x -> x > growthFactor);

        assertTrue(anyAboveGrowthRate);
    }

}
