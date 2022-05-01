package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    @Test
    public void removeOldDirTest() {
        File myDir = new File("./code2vec/data/test_1/");
        File currDir = new File("./code2vec/data/" + GenotypeSupport.getCurrentDataset());
        myDir.mkdir();
        assertTrue(myDir.exists());
        assertTrue(currDir.exists());
        GenotypeSupport.removeOtherDirs();
        assertFalse(myDir.exists());
        assertTrue(currDir.exists());
    }

    @Test
    public void initializeFieldsTest() {
        GenotypeSupport.setConfigFile("src/test/resources/config.properties");
        assertTrue(GenotypeSupport.getMetrics().isEmpty());
        Properties prop = GenotypeSupport.initializeFields();
        assertEquals(prop.getProperty("version"), "1.0");
        assertFalse(GenotypeSupport.getMetrics().isEmpty());
    }

    @Test
    public void fillFitnessTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        GenotypeSupport.fillFitness(transformers, 8);
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertEquals(GenotypeSupport.getMetricResult(transformers).get(), 8.0);
        transformers.add(new RandomParameterNameTransformer());
        GenotypeSupport.fillFitness(transformers, 10);
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertEquals(GenotypeSupport.getMetricResult(transformers).get(), 10.0);
    }

    @Test
    public void storeFilesTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        GenotypeSupport.storeFiles(transformers, "file", 10.0);
        assertTrue(GenotypeSupport.getDir(transformers).isPresent());
        assertEquals(GenotypeSupport.getDir(transformers).get(), "file");
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertEquals(GenotypeSupport.getMetricResult(transformers).get(), 10.0);
    }

    @Test
    public void runTransformationsTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        String name = GenotypeSupport.runTransformations(transformers, "generation_0");
        assertTrue(GenotypeSupport.getDir(transformers).isPresent());
        assertEquals(GenotypeSupport.getDir(transformers).get(), name);
        GenotypeSupport.removeOtherDirs();
    }
}
