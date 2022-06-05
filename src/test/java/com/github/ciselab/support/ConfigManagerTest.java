package com.github.ciselab.support;

import static com.github.ciselab.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {

    GenotypeSupport genotypeSupport;
    MetricCache metricCache;
    ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        metricCache = new MetricCache();
        genotypeSupport = new GenotypeSupport(metricCache);
        configManager = genotypeSupport.getConfigManager();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Test
    public void initializeFieldsTest_withMetrics() {
        configManager.setConfigFile("src/test/resources/config.properties");
        Properties prop = configManager.initializeFields();
        assertEquals(prop.getProperty("version"), "1.0");
        assertFalse(metricCache.getMetrics().isEmpty());
        assertFalse(metricCache.getWeights().isEmpty());
        assertEquals(configManager.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Test
    public void initializeFieldsTest_withoutMetrics() {
        configManager.setConfigFile("src/test/resources/noMetrics.properties");
        assertThrows(IllegalArgumentException.class, () -> configManager.initializeFields());
    }

    @Test
    public void initializeFieldsTest_wrongFile_throwsNullPointerException() {
        configManager.setConfigFile("src/test/resources/wrongFile.properties");
        assertThrows(NullPointerException.class, () -> configManager.initializeFields());
    }
}
