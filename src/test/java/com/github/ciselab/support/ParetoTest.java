package com.github.ciselab.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.ciselab.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.*;

public class ParetoTest {

    Pareto pareto;
    ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        pareto = new Pareto(metricCache);
        configManager = genotypeSupport.getConfigManager();
        configManager.setConfigFile("src/test/resources/config.properties");
        configManager.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Test
    public void addToParetoOptimumTest_basics_withMaximize() {
        configManager.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        pareto.setPareto(initial);
        assertSame(pareto.getPareto(), initial);
        pareto.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{3.0, 1.0}));

        pareto.addToParetoOptimum(new double[]{5.0, 1.0});
        assertEquals(3, pareto.getPareto().size());
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{5.0, 1.0}));
    }

    @Test
    public void addToParetoOptimumTest_basics_withMinimize() {
        configManager.setMaximize(false);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        pareto.setPareto(initial);
        assertSame(pareto.getPareto(), initial);
        pareto.addToParetoOptimum(new double[]{5.0, 1.0});
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{3.0, 1.0}));

        pareto.addToParetoOptimum(new double[]{1.0, 2.0});
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{3.0, 1.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 2.0}));

        pareto.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 1.0}));
    }

    @Test
    public void addToParetoTest_addUltimateSolution_withMaximize() {
        configManager.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        pareto.setPareto(initial);
        assertSame(pareto.getPareto(), initial);
        pareto.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, pareto.getPareto().size());
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{5.0, 5.0}));
    }

    @Test
    public void addToParetoTest_addExistingArray() {
        configManager.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        pareto.setPareto(initial);
        pareto.addToParetoOptimum(new double[]{1.0, 3.0});
        assertEquals(3, pareto.getPareto().size());
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(pareto.isIn(pareto.getPareto(), new double[]{3.0, 1.0}));
        pareto.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, pareto.getPareto().size());
        pareto.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, pareto.getPareto().size());
    }
}
