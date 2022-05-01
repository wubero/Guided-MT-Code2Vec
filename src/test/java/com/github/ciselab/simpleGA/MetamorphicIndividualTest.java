package com.github.ciselab.simpleGA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jenetics.prngine.LCG64ShiftRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

public class MetamorphicIndividualTest {

    RandomGenerator r = new LCG64ShiftRandom(101010);

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
}
