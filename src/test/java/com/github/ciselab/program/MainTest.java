package com.github.ciselab.program;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicPopulation;
import com.github.ciselab.lampion.guided.program.Main;
import com.github.ciselab.lampion.guided.support.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final ConfigManagement configManagement = genotypeSupport.getConfigManagement();
    private Properties prop;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        configManagement.setConfigFile("src/test/resources/config.properties");
        prop = configManagement.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
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
        configManagement.setMaximize(true);
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(3.0, Main.getBestForLog(arr));
    }

    @Test
    public void getWorstTest() {
        configManagement.setMaximize(true);
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(0.8, Main.getWorstForLog(arr));
    }

    @Test
    public void getMedianTest() {
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(1.3, Main.getMedianForLog(arr));
    }

    @Test
    public void getAverageTest() {
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(1.3);
        arr.add(0.8);
        arr.add(3.0);
        assertEquals(1.7, Main.getAverageForLog(arr));
    }
}
