package com.github.ciselab.program;

import com.github.ciselab.simpleGA.MetamorphicIndividual;
import com.github.ciselab.simpleGA.MetamorphicPopulation;
import com.github.ciselab.support.FileManagement;
import com.github.ciselab.support.GenotypeSupport;
import java.time.LocalTime;
import java.util.Properties;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private GenotypeSupport genotypeSupport = new GenotypeSupport();
    private Properties prop;

    @BeforeEach
    public void setUp() {
        genotypeSupport.setConfigFile("src/test/resources/config.properties");
        prop = genotypeSupport.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(genotypeSupport.getDataDir());
    }

    @Test
    public void isFitterTest_isFitter() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(1, r, 6, false, genotypeSupport);
        MetamorphicIndividual indiv = new MetamorphicIndividual(genotypeSupport);
        indiv.createIndividual(r, 1, 6);
        pop.saveIndividual(0, indiv);
        indiv.setFitness(0.5);
        double best = -1;
        genotypeSupport.setMaximize(true);
        assertTrue(Main.isFitter(pop, best));
    }

    @Test
    public void isFitterTest_isNotFitter() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicPopulation pop = new MetamorphicPopulation(1, r, 6, false, genotypeSupport);
        MetamorphicIndividual indiv = new MetamorphicIndividual(genotypeSupport);
        indiv.createIndividual(r, 1, 6);
        pop.saveIndividual(0, indiv);
        indiv.setFitness(0.5);
        double best = 1;
        genotypeSupport.setMaximize(true);
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
        Main.setMaxGenerations(1);
        Main.setPopSize(1);
        Main.runSimpleGA(prop);
    }
}
