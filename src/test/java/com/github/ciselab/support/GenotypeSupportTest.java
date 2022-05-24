package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    GenotypeSupport genotypeSupport;

    @BeforeEach
    public void setUp() {
        genotypeSupport = new GenotypeSupport();
        genotypeSupport.setConfigFile("src/test/resources/config.properties");
        genotypeSupport.initializeFields();
    }

    @AfterEach
    public void after() {
        genotypeSupport.removeOtherDirs();
    }

    @Tag("File")
    @Test
    public void removeOldDirTest() {
        File myDir = new File("./code2vec/data/test_1/");
        File currDir = new File("./code2vec/data/" + genotypeSupport.getCurrentDataset());
        myDir.mkdir();
        assertTrue(myDir.exists());
        assertTrue(currDir.exists());
        genotypeSupport.removeOtherDirs();
        assertFalse(myDir.exists());
        assertTrue(currDir.exists());
    }

    @Test
    public void initializeFieldsTest() {
        genotypeSupport.setConfigFile("src/test/resources/config.properties");
        Properties prop = genotypeSupport.initializeFields();
        assertEquals(prop.getProperty("version"), "1.0");
        assertFalse(genotypeSupport.getMetrics().isEmpty());
        assertFalse(genotypeSupport.getWeights().isEmpty());
        assertEquals(genotypeSupport.getSeed(), Long.parseLong(prop.getProperty("seed")));
    }

    @Test
    public void fillFitnessTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        genotypeSupport.fillFitness(transformers, arr);
        assertTrue(genotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(genotypeSupport.getMetricResult(transformers).get(), arr);
        transformers.add(new RandomParameterNameTransformer());
        double[] second = new double[]{9,4,5};
        genotypeSupport.fillFitness(transformers, second);
        assertTrue(genotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(genotypeSupport.getMetricResult(transformers).get(), second);
    }

    @Test
    public void storeFilesTest() {
        List<BaseTransformer> transformers = new ArrayList<>();
        double[] arr = new double[]{10,3};
        genotypeSupport.storeFiles(transformers, "file", arr);
        assertTrue(genotypeSupport.getDir(transformers).isPresent());
        assertEquals(genotypeSupport.getDir(transformers).get(), "file");
        assertTrue(genotypeSupport.getMetricResult(transformers).isPresent());
        assertSame(genotypeSupport.getMetricResult(transformers).get(), arr);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runTransformationsTest() throws IOException {
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(genotypeSupport.getDataDir() + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = genotypeSupport.runTransformations(transformers, "code_files");
        assertTrue(genotypeSupport.getDir(transformers).isPresent());
        assertEquals(genotypeSupport.getDir(transformers).get(), name);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runCode2vecInferenceTest() throws IOException {
        genotypeSupport.setConfigFile("src/test/resources/config.properties");
        genotypeSupport.initializeFields();
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(genotypeSupport.getDataDir() + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = genotypeSupport.runTransformations(transformers, "code_files");
        genotypeSupport.runCode2vec(name);
        assertTrue(genotypeSupport.getTotalCode2vevTime() > 0);
    }

    @Test
    public void addToParetoOptimumTest_basics_withMaximize() {
        genotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        genotypeSupport.setPareto(initial);
        assertSame(genotypeSupport.getPareto(), initial);
        genotypeSupport.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{3.0, 1.0}));

        genotypeSupport.addToParetoOptimum(new double[]{5.0, 1.0});
        assertEquals(3, genotypeSupport.getPareto().size());
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{5.0, 1.0}));
    }

    @Test
    public void addToParetoOptimumTest_basics_withMinimize() {
        genotypeSupport.setMaximize(false);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        genotypeSupport.setPareto(initial);
        assertSame(genotypeSupport.getPareto(), initial);
        genotypeSupport.addToParetoOptimum(new double[]{5.0, 1.0});
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{3.0, 1.0}));

        genotypeSupport.addToParetoOptimum(new double[]{1.0, 2.0});
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{3.0, 1.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 2.0}));

        genotypeSupport.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 1.0}));
    }

    @Test
    public void addToParetoTest_addUltimateSolution_withMaximize() {
        genotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        genotypeSupport.setPareto(initial);
        assertSame(genotypeSupport.getPareto(), initial);
        genotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, genotypeSupport.getPareto().size());
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{5.0, 5.0}));
    }

    @Test
    public void addToParetoTest_addExistingArray() {
        genotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        genotypeSupport.setPareto(initial);
        genotypeSupport.addToParetoOptimum(new double[]{1.0, 3.0});
        assertEquals(3, genotypeSupport.getPareto().size());
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(genotypeSupport.isIn(genotypeSupport.getPareto(), new double[]{3.0, 1.0}));
        genotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, genotypeSupport.getPareto().size());
        genotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, genotypeSupport.getPareto().size());
    }

    @Test
    public void setDataDirTest() {
        File[] files = new File("src/test/resources/code_files").listFiles();
        if(files!=null) {
            int l = files.length;
            genotypeSupport.setDataDir(genotypeSupport.dir_path + "/src/test/resources/code_files");
            File[] dataFiles = new File(genotypeSupport.getDataDir() + "generation_0").listFiles();
            assertNotNull(dataFiles);
            assertEquals(l, dataFiles.length);
        } else {
            fail("This directory shouldn't be empty");
        }
    }
}
