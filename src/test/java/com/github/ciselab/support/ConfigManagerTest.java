package com.github.ciselab.support;

import static com.github.ciselab.lampion.guided.support.FileManagement.dataDir;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.util.Properties;

import com.github.ciselab.lampion.guided.support.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {


    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_withMetrics() throws FileNotFoundException {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/config.properties");
        Properties prop = configManagement.initializeFields();


        assertEquals( "1.1",prop.getProperty("version"));
        assertFalse(metricCache.getMetrics().isEmpty());
        assertFalse(metricCache.getWeights().isEmpty());
        assertEquals(configManagement.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_withMetrics_negativeMetric() throws FileNotFoundException {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/negativeMetric.properties");
        Properties prop = configManagement.initializeFields();


        assertEquals( "1.1",prop.getProperty("version"));
        assertFalse(metricCache.getMetrics().isEmpty());
        assertFalse(metricCache.getWeights().isEmpty());
        assertEquals(configManagement.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingGAs_shouldHaveGA() throws FileNotFoundException {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/ga.properties");
        Properties prop = configManagement.initializeFields();


        assertTrue(configManagement.getUseGa());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingRandom_shouldHaveRandom() throws FileNotFoundException {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/random.properties");
        Properties prop = configManagement.initializeFields();

        assertFalse(configManagement.getUseGa());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingGAs2_shouldHaveGA() throws FileNotFoundException {
        // File has different spelling of True
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/ga2.properties");
        Properties prop = configManagement.initializeFields();


        assertTrue(configManagement.getUseGa());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingRandom2_shouldHaveRandom() throws FileNotFoundException {
        // File has different spelling of False
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/random2.properties");
        Properties prop = configManagement.initializeFields();

        assertFalse(configManagement.getUseGa());
    }
    @Tag("File")
    @Test
    public void initializeFieldsTest_withoutMetrics() {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/noMetrics.properties");
        assertThrows(IllegalArgumentException.class, () -> configManagement.initializeFields());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_wrongFile_throwsNullPointerException() {
        MetricCache metricCache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(metricCache);
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();

        configManagement.setConfigFile("src/test/resources/config_examples/wrongFile.properties");

        assertThrows(FileNotFoundException.class, () -> configManagement.initializeFields());
    }

}
