package com.github.ciselab.algorithms;

import com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetamorphicIndividualTest {

    @Test
    public void testPopulateIndividual_with3Transformers_shouldHave3Transformations(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        a.populateIndividual(r,3,20);

        assertEquals(3,a.getLength());
    }

    @Test
    public void testPopulateIndividual_with1Transformers_shouldHave1Transformations(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        a.populateIndividual(r,3,20);

        assertEquals(1,a.getLength());
    }

    @Test
    public void testGetGeneration_ShouldReturnValueFromConstructor(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,10);

        assertEquals(10,a.getGeneration());
    }

    @Test
    public void testGetPaths_freshGene_ShouldNotHaveAnyPaths(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,10);

        assertTrue(a.getResultPath().isEmpty());
        assertTrue(a.getJavaPath().isEmpty());
    }

    @Test
    public void testGetParents_freshGene_shouldNotHaveAnyParents(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,10);

        assertTrue(a.getParents().isEmpty());
    }

    @Test
    public void testGetParents_freshGene_shouldHaveLength0(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,10);

        assertEquals(0,a.getLength());
    }

    @Test
    public void testPopulateIndividual_with3Transformers_getGeneShouldReturnValue(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        a.populateIndividual(r,3,20);

        assertNotNull(a.getGene(0));
    }


    @Test
    public void testAddGene_getGeneShouldReturnValue(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual testObject = new MetamorphicIndividual(support,0);

        BaseTransformer a = new IfTrueTransformer(10);
        testObject.addGene(a);

        assertNotNull(testObject.getGene(0));
        assertEquals(a,testObject.getGene(0));
    }

    @Test
    public void testAddGene_sizeShouldGrow(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual testObject = new MetamorphicIndividual(support,0);

        BaseTransformer a = new IfTrueTransformer(10);
        testObject.addGene(a);

        assertEquals(1,testObject.getLength());
    }

    @Test
    public void testAddGene_twoGenes_sizeShouldGrow(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual testObject = new MetamorphicIndividual(support,0);

        BaseTransformer a = new IfTrueTransformer(10);
        BaseTransformer b = new IfTrueTransformer(11);
        testObject.addGene(a);
        testObject.addGene(b);

        assertEquals(2,testObject.getLength());
    }

    @Test
    public void testAddGene_addTwoGenes_getGeneShouldReturnValue(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual testObject = new MetamorphicIndividual(support,0);

        BaseTransformer a = new IfTrueTransformer(10);
        BaseTransformer b = new AddNeutralElementTransformer(11);
        testObject.addGene(a);
        testObject.addGene(b);

        assertNotNull(testObject.getGene(0));
        assertEquals(a,testObject.getGene(0));
        assertNotNull(testObject.getGene(1));
        assertEquals(b,testObject.getGene(1));
    }

    /* ===================================================
            Equality and HashCode Tests
       ====================================================
     */

    @Tag("Regression")
    @Test
    public void testEquality_TwoEmptyIndividuals_areEqual(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);

        assertEquals(a,b);
    }

    @Tag("Regression")
    @Test
    public void testEquality_toItself_isEqual(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        assertEquals(a,a);
    }

    @Tag("Regression")
    @Test
    public void testEquality_twoDifferentGenes_areNotEqual(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,2,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,2,20);

        assertNotEquals(a,b);
    }

    @Tag("Regression")
    @Test
    public void testEquality_twoGenesWithSameTransformers_areEqual(){
        RandomGenerator r = new Random(5);
        RandomGenerator p = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,2,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(p,2,20);

        assertEquals(a,b);
    }

    @Tag("Regression")
    @Test
    public void testEquality_twoDifferentGenes_areNotEqual_test2(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,10,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,10,20);

        assertNotEquals(a,b);
    }

    @Tag("Regression")
    @Test
    public void testEquality_twoGenesWithSameTransformers_areEqual_test2(){
        RandomGenerator r = new Random(5);
        RandomGenerator p = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,10,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(p,10,20);

        assertEquals(a,b);
    }


    @Tag("Regression")
    @Test
    public void testHashCode_TwoEmptyIndividuals_areEqual(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Tag("Regression")
    @Test
    public void testHashCode_toItself_isEqual(){
        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);

        assertEquals(a.hashCode(),a.hashCode());
    }

    @Tag("Regression")
    @Test
    public void testHashCode_twoDifferentGenes_areNotEqual(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,2,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,2,20);

        assertNotEquals(a.hashCode(),b.hashCode());
    }

    @Tag("Regression")
    @Test
    public void testHashCode_twoGenesWithSameTransformers_areEqual(){
        RandomGenerator r = new Random(5);
        RandomGenerator p = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,2,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(p,2,20);

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Tag("Regression")
    @Test
    public void testHashcode_twoDifferentGenes_areNotEqual_test2(){
        RandomGenerator r = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,10,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(r,10,20);

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Tag("Regression")
    @Test
    public void testHashCode_twoGenesWithSameTransformers_areEqual_test2(){
        RandomGenerator r = new Random(5);
        RandomGenerator p = new Random(5);

        var config = new Configuration();
        MetricCache cache = makeEmptyCache();
        GenotypeSupport support = new GenotypeSupport(cache,config);

        MetamorphicIndividual a = new MetamorphicIndividual(support,0);
        a.populateIndividual(r,10,20);
        MetamorphicIndividual b = new MetamorphicIndividual(support,0);
        b.populateIndividual(p,10,20);

        assertNotEquals(a.hashCode(),b.hashCode());
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
