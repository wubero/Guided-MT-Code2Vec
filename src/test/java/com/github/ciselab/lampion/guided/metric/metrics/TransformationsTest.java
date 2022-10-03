package com.github.ciselab.lampion.guided.metric.metrics;

import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.Transformations;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationsTest {

    @Test
    public void checkNameTest() {
        Transformations metric = new Transformations();
        assertEquals("TRANSFORMATIONS", metric.getName());
    }

    @Test
    public void testCanBeBiggerThanOne_shouldBe(){
        Transformations metric = new Transformations();
        assertTrue(metric.canBeBiggerThanOne());
    }

    @Test
    public void testIsSecondary_shouldBe(){
        Transformations metric = new Transformations();
        assertTrue(metric.isSecondary());
    }

    @Test
    public void testApply_EmptyGene_shouldBe0(){
        var support = makeNewEmptySupport();

        MetamorphicIndividual individual = new MetamorphicIndividual(support,0);

        Metric testObject = new Transformations();

        var result = testObject.apply(individual);

        assertEquals(0.0,result,0.0001);
    }

    @Test
    public void testApply_GeneWithOneTransformer_shouldBe1(){
        var support = makeNewEmptySupport();

        MetamorphicIndividual individual = new MetamorphicIndividual(support,0);
        individual.addGene(new IfTrueTransformer(5));

        Metric testObject = new Transformations();

        var result = testObject.apply(individual);

        assertEquals(1.0,result,0.0001);
    }

    @Test
    public void testApply_GeneWithThreeTransformers_shouldBe3(){
        var support = makeNewEmptySupport();

        MetamorphicIndividual individual = new MetamorphicIndividual(support,0);
        individual.addGene(new IfTrueTransformer(5));
        individual.addGene(new IfTrueTransformer(5));
        individual.addGene(new IfTrueTransformer(5));

        Metric testObject = new Transformations();

        var result = testObject.apply(individual);

        assertEquals(3.0,result,0.0001);
    }

    @Test
    public void testEquality_isEqualToItself(){
        Metric metric = new Transformations();

        assertEquals(metric,metric);
    }

    @Test
    public void testEquality_sameWeight_isEqual(){
        Metric a = new Transformations();
        Metric b = new Transformations();

        assertEquals(a,b);
    }

    @Test
    public void testEquality_differentWeight_notEqual(){
        Metric a = new Transformations();
        a.setWeight(0.75);
        Metric b = new Transformations();
        b.setWeight(0.5);
        assertNotEquals(a,b);
    }

    @Test
    public void testEquality_againstNonMetric_isNotEqual(){
        Metric metric = new Transformations();
        Double other = 5.0;
        assertNotEquals(other,metric);
    }

    @Test
    public void testHashCode_isEqualToItself(){
        Metric metric = new Transformations();

        assertEquals(metric.hashCode(),metric.hashCode());
    }

    @Test
    public void testHashCode_sameWeight_isEqual(){
        Metric a = new Transformations();
        Metric b = new Transformations();

        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void testHashCode_differentWeight_notEqual(){
        Metric a = new Transformations();
        a.setWeight(0.75);
        Metric b = new Transformations();
        b.setWeight(0.5);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

    static GenotypeSupport makeNewEmptySupport(){
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        cache.getMetrics().removeIf(x ->true);

        GenotypeSupport support = new GenotypeSupport(cache,config);
        return support;
    }

}
