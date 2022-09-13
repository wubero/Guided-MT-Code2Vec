package com.github.ciselab.support;

import static com.github.ciselab.lampion.guided.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import com.github.ciselab.lampion.guided.support.ConfigManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {

    GenotypeSupport genotypeSupport;
    MetricCache metricCache;
    ConfigManagement configManagement;

    @BeforeEach
    public void setUp() {
        metricCache = new MetricCache();
        genotypeSupport = new GenotypeSupport(metricCache);
        configManagement = genotypeSupport.getConfigManagement();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Test
    public void initializeFieldsTest_withMetrics() {
        configManagement.setConfigFile("src/test/resources/config.properties");
        Properties prop = configManagement.initializeFields();
        assertEquals(prop.getProperty("version"), "1.0");
        assertFalse(metricCache.getMetrics().isEmpty());
        assertFalse(metricCache.getWeights().isEmpty());
        assertEquals(configManagement.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Test
    public void initializeFieldsTest_withoutMetrics() {
        configManagement.setConfigFile("src/test/resources/noMetrics.properties");
        assertThrows(IllegalArgumentException.class, () -> configManagement.initializeFields());
    }

    @Test
    public void initializeFieldsTest_wrongFile_throwsNullPointerException() {
        configManagement.setConfigFile("src/test/resources/wrongFile.properties");
        assertThrows(NullPointerException.class, () -> configManagement.initializeFields());
    }
}
