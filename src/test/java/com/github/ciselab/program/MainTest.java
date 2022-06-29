package com.github.ciselab.program;

import com.github.ciselab.simpleGA.MetamorphicIndividual;
import com.github.ciselab.simpleGA.MetamorphicPopulation;
import com.github.ciselab.support.ConfigManager;
import com.github.ciselab.support.FileManagement;
import com.github.ciselab.support.GenotypeSupport;
import com.github.ciselab.support.MetricCache;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private final MetricCache cache = new MetricCache();
    private final GenotypeSupport genotypeSupport = new GenotypeSupport(cache);
    private final ConfigManager configManager = genotypeSupport.getConfigManager();
    private Properties prop;

    @BeforeEach
    public void setUp() {
        configManager.setConfigFile("src/test/resources/config.properties");
        prop = configManager.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
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
        configManager.setMaximize(true);
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
        configManager.setMaximize(true);
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
        Main.runSimpleGA();
    }

    @Test
    public void getBestTest_maximize() {
        configManager.setMaximize(true);
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(3.0, Main.getBest(arr));
    }

    @Test
    public void getWorstTest() {
        configManager.setMaximize(true);
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(0.8, Main.getWorst(arr));
    }

    @Test
    public void getMedianTest() {
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(1.3, Main.getMedian(arr));
    }

    @Test
    public void getAverageTest() {
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(1.7, Main.getAverage(arr));
    }
}
