package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    @AfterEach
    public void after() {
        GenotypeSupport.removeOtherDirs();
    }

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
        Properties prop = GenotypeSupport.initializeFields();
        assertEquals(prop.getProperty("version"), "1.0");
        assertFalse(GenotypeSupport.getMetrics().isEmpty());
        assertFalse(GenotypeSupport.getWeights().isEmpty());
        assertEquals(GenotypeSupport.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Test
    public void initializeFields_withNoMetrics_Test() {
        GenotypeSupport.clearLists();
        GenotypeSupport.setConfigFile("src/test/resources/config2.properties");
        assertThrows(IllegalArgumentException.class, GenotypeSupport::initializeFields);
    }

    @Test
    public void fillFitnessTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        GenotypeSupport.fillFitness(transformers, arr);
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(GenotypeSupport.getMetricResult(transformers).get(), arr);
        transformers.add(new RandomParameterNameTransformer());
        double[] second = new double[]{9,4,5};
        GenotypeSupport.fillFitness(transformers, second);
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(GenotypeSupport.getMetricResult(transformers).get(), second);
    }

    @Test
    public void storeFilesTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        GenotypeSupport.storeFiles(transformers, "file", arr);
        assertTrue(GenotypeSupport.getDir(transformers).isPresent());
        assertEquals(GenotypeSupport.getDir(transformers).get(), "file");
        assertTrue(GenotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(GenotypeSupport.getMetricResult(transformers).get(), arr);
    }

    @Test
    public void runTransformationsTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        String name = GenotypeSupport.runTransformations(transformers, "generation_0");
        assertTrue(GenotypeSupport.getDir(transformers).isPresent());
        assertEquals(GenotypeSupport.getDir(transformers).get(), name);
        assertTrue(GenotypeSupport.getTotalTransformationTime() > 0);
        GenotypeSupport.removeOtherDirs();
    }

    @Test
    public void runCode2vecInferenceTest() {
        GenotypeSupport.setConfigFile("src/test/resources/config.properties");
        GenotypeSupport.initializeFields();
        List<BaseTransformer> transformers = new ArrayList<>();
        String name = GenotypeSupport.runTransformations(transformers, "generation_0");
        GenotypeSupport.runCode2vec(name);
        assertTrue(GenotypeSupport.getTotalCode2vevTime() > 0);
        GenotypeSupport.removeOtherDirs();
    }
}
