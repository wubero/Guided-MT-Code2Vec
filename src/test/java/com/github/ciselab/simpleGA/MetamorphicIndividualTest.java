package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.support.GenotypeSupport;
import io.jenetics.prngine.LCG64ShiftRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MetamorphicIndividualTest {

    RandomGenerator r = new LCG64ShiftRandom(101010);

    @BeforeEach
    public void setUp() {
        GenotypeSupport.setConfigFile("src/test/resources/config.properties");
        GenotypeSupport.initializeFields();
    }

    @AfterEach
    public void after() {
        GenotypeSupport.removeOtherDirs();
    }

    @Test
    public void createIndividualTest() {
        MetamorphicIndividual individual = new MetamorphicIndividual();
        assertTrue(individual.getTransformers().isEmpty());
        individual.createIndividual(r, 2, 6);
        assertFalse(individual.getTransformers().isEmpty());
    }

    @Test
    public void increaseIndividualLength() {
        MetamorphicIndividual individual = new MetamorphicIndividual();
        individual.createIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.increase(10, r, 6);
        assertEquals(individual.getTransformers().size(), 3);
    }

    @Test
    public void decreaseIndividualLength() {
        MetamorphicIndividual individual = new MetamorphicIndividual();
        individual.createIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.decrease(r);
        assertEquals(individual.getTransformers().size(), 1);
    }

    @Test
    @Tag("slow")
    public void extendExistingDirectory_withTransformerTest() {
        MetamorphicIndividual individual = new MetamorphicIndividual();
        individual.createIndividual(r, 2, 6);
        individual.getFitness();
        individual.addGene(individual.createGene(3, r));
        individual.getFitness();
        assertEquals(individual.getGene(2).getClass(), AddNeutralElementTransformer.class);
        individual.setGene(2, individual.createGene(0, r));
        assertEquals(individual.getGene(2).getClass(), IfTrueTransformer.class);
    }
}
