package com.github.ciselab.program;

import com.github.ciselab.simpleGA.MetamorphicIndividual;
import com.github.ciselab.simpleGA.MetamorphicPopulation;
import com.github.ciselab.support.GenotypeSupport;
import java.time.LocalTime;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

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
    public void isFitterTest_isFitter() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(1, r, 6, false);
        MetamorphicIndividual indiv = new MetamorphicIndividual();
        indiv.createIndividual(r, 1, 6);
        pop.saveIndividual(0, indiv);
        indiv.setFitness(0.5);
        double best = -1;
        GenotypeSupport.setMaximize(true);
        assertTrue(Main.isFitter(pop, best));
    }

    @Test
    public void isFitterTest_isNotFitter() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(1, r, 6, false);
        MetamorphicIndividual indiv = new MetamorphicIndividual();
        indiv.createIndividual(r, 1, 6);
        pop.saveIndividual(0, indiv);
        indiv.setFitness(0.5);
        double best = 1;
        GenotypeSupport.setMaximize(true);
        assertFalse(Main.isFitter(pop, best));
    }

    @Test
    public void timeDiffSmallerTest_isSmaller() {
        LocalTime time = LocalTime.now().minusMinutes(2);
        Main.setMaxTimeInMin(5);
        assertTrue(Main.timeDiffSmaller(time));
    }

    @Test
    public void timeDiffSmallerTest_isNotSmaller() {
        LocalTime time = LocalTime.now().minusMinutes(2);
        Main.setMaxTimeInMin(1);
        assertFalse(Main.timeDiffSmaller(time));
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void mainIntegrationTest() {
        Main.setMaxGenerations(2);
        Main.setPopSize(1);
        Main.runSimpleGA();
    }
}
