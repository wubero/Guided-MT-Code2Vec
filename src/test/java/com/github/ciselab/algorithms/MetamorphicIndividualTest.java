package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.support.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MetamorphicIndividualTest {

    private GenotypeSupport genotypeSupport;
    private MetamorphicIndividual individual;

    @BeforeEach
    public void setUp() {
        genotypeSupport = new GenotypeSupport(new MetricCache());
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
        individual = new MetamorphicIndividual(genotypeSupport);
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void createIndividualTest() {
        RandomGenerator r = new SplittableRandom(101010);
        assertTrue(individual.getTransformers().isEmpty());
        individual.populateIndividual(r, 2, 6);
        assertFalse(individual.getTransformers().isEmpty());
    }

    @Test
    public void increaseIndividualLength() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.increase(10, r, 6);
        assertEquals(individual.getTransformers().size(), 3);
    }

    @Test
    public void decreaseIndividualLength() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.decrease(r);
        assertEquals(individual.getTransformers().size(), 1);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void extendExistingDirectory_withTransformerTest() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        individual.getFitness();
        individual.addGene(individual.createGene(3, r));
        individual.getFitness();
        assertEquals(individual.getGene(2).getClass(), AddNeutralElementTransformer.class);
        individual.setGene(2, individual.createGene(0, r));
        assertEquals(individual.getGene(2).getClass(), IfTrueTransformer.class);
    }
}
