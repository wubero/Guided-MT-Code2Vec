package com.github.ciselab.support;

import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {

    @Tag("File")
    @Test
    public void initializeMetricTest_withMetrics_negativeMetric() throws IOException {
        String path = "src/test/resources/config_examples/negativeMetric.properties";

        var testObject = ConfigManagement.initializeMetricCache(path);

        assertFalse(testObject.getMetrics().isEmpty());
    }

    @Tag("File")
    @Test
    public void initializeMetricTest_usingGAs_shouldHaveGA() throws IOException {
        String path = "src/test/resources/config_examples/ga.properties";

        var config = ConfigManagement.readConfig(path);

        assertTrue(config.program.useGA());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingRandom_shouldHaveNoGA() throws IOException {
        String path = "src/test/resources/config_examples/random.properties";

        var config = ConfigManagement.readConfig(path);

        assertFalse(config.program.useGA());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingGAs_differentSpelling_shouldHaveGA() throws IOException {
        String path = "src/test/resources/config_examples/ga2.properties";

        var config = ConfigManagement.readConfig(path);

        assertTrue(config.program.useGA());
    }

    @Tag("File")
    @Test
    public void initializeFieldsTest_usingRandom_differentSpelling_shouldHaveNoGA() throws IOException {
        String path = "src/test/resources/config_examples/random2.properties";

        var config = ConfigManagement.readConfig(path);

        assertFalse(config.program.useGA());
    }

    @Tag("File")
    @Test
    public void readConfigTest_configHasProgramValues_DefaultsAreOverwritten()throws IOException {
        String path = "src/test/resources/config_examples/program.properties";

        var config = ConfigManagement.readConfig(path);

        assertTrue(config.program.useGA());
        assertEquals(105,config.program.getSeed());
        assertEquals("F:/My/Path",config.program.getBashPath());
    }


    @Tag("File")
    @Test
    public void readConfigTest_configHasGeneticValues_DefaultsAreOverwritten() throws IOException {
        String path = "src/test/resources/config_examples/genetic.properties";

        var config = ConfigManagement.readConfig(path);

        assertEquals(0.99, config.genetic.getCrossoverRate(),0.01);
        assertEquals(0.5, config.genetic.getElitismRate(),0.01);
        assertEquals(0.15, config.genetic.getMutationRate(),0.01);
        assertEquals(0.4, config.genetic.getIncreaseSizeRate(),0.01);

        assertEquals(25, config.genetic.getMaxGeneLength());
        assertEquals(12, config.genetic.getPopSize());
        assertEquals(6, config.genetic.getTournamentSize());
        assertEquals(4, config.genetic.getMaxSteadyGenerations());
    }

    @Tag("File")
    @Test
    public void initializeCache_configHasNoMetrics_CacheHasNoMetrics() throws IOException {
        String path = "src/test/resources/config_examples/noMetrics.properties";

        assertThrows(IllegalArgumentException.class, () -> ConfigManagement.initializeMetricCache(path));
    }

    @Tag("File")
    @Test
    public void readConfig_InvalidPath_ThrowsException(){
        String path = "src/test/resources/config_examples/doesnotexist.properties";

        assertThrows(IOException.class, () -> ConfigManagement.readConfig(path));
    }

    @Tag("File")
    @Test
    public void initializeCache_InvalidPath_ThrowsException(){
        String path = "src/test/resources/config_examples/doesnotexist.properties";

        assertThrows(IOException.class, () -> ConfigManagement.initializeMetricCache(path));
    }
}
