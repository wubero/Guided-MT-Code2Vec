package com.github.ciselab.support;

import com.github.ciselab.lampion.guided.support.ConfigManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.ciselab.lampion.guided.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.*;

public class ParetoFrontTest {

    ParetoFront paretoFront;
    ConfigManagement configManagement;

    @BeforeEach
    public void setUp() {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        paretoFront = new ParetoFront(metricCache);
        configManagement = genotypeSupport.getConfigManagement();
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Test
    public void addToParetoOptimumTest_basics_withMaximize() {
        configManagement.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        paretoFront.setFrontier(initial);
        assertSame(paretoFront.getFrontier(), initial);
        paretoFront.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 3.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{2.0, 2.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{3.0, 1.0}));

        paretoFront.addToParetoOptimum(new double[]{5.0, 1.0});
        assertEquals(3, paretoFront.getFrontier().size());
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 3.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{2.0, 2.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{5.0, 1.0}));
    }

    @Test
    public void addToParetoOptimumTest_basics_withMinimize() {
        configManagement.setMaximize(false);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        paretoFront.setFrontier(initial);
        assertSame(paretoFront.getFrontier(), initial);
        paretoFront.addToParetoOptimum(new double[]{5.0, 1.0});
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 3.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{2.0, 2.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{3.0, 1.0}));

        paretoFront.addToParetoOptimum(new double[]{1.0, 2.0});
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{3.0, 1.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 2.0}));

        paretoFront.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 1.0}));
    }

    @Test
    public void addToParetoTest_addUltimateSolution_withMaximize() {
        configManagement.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        paretoFront.setFrontier(initial);
        assertSame(paretoFront.getFrontier(), initial);
        paretoFront.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, paretoFront.getFrontier().size());
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{5.0, 5.0}));
    }

    @Test
    public void addToParetoTest_addExistingArray() {
        configManagement.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        paretoFront.setFrontier(initial);
        paretoFront.addToParetoOptimum(new double[]{1.0, 3.0});
        assertEquals(3, paretoFront.getFrontier().size());
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{1.0, 3.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{2.0, 2.0}));
        assertTrue(paretoFront.isIn(paretoFront.getFrontier(), new double[]{3.0, 1.0}));
        paretoFront.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, paretoFront.getFrontier().size());
        paretoFront.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, paretoFront.getFrontier().size());
    }
}
