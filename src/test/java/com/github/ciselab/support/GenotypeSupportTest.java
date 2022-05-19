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

import static com.github.ciselab.support.GenotypeSupport.isIn;
import static com.github.ciselab.support.GenotypeSupport.setDataDir;
import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    @BeforeEach
    public void setUp() {
        GenotypeSupport.setConfigFile("src/test/resources/config.properties");
        GenotypeSupport.initializeFields();
    }

    @AfterEach
    public void after() {
        GenotypeSupport.removeOtherDirs();
    }

    @Tag("File")
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

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runTransformationsTest() throws IOException {
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(GenotypeSupport.dataDir + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = GenotypeSupport.runTransformations(transformers, "code_files");
        assertTrue(GenotypeSupport.getDir(transformers).isPresent());
        assertEquals(GenotypeSupport.getDir(transformers).get(), name);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runCode2vecInferenceTest() throws IOException {
        GenotypeSupport.setConfigFile("src/test/resources/config.properties");
        GenotypeSupport.initializeFields();
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(GenotypeSupport.dataDir + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = GenotypeSupport.runTransformations(transformers, "code_files");
        GenotypeSupport.runCode2vec(name);
        assertTrue(GenotypeSupport.getTotalCode2vevTime() > 0);
    }

    @Test
    public void addToParetoOptimumTest_basics_withMaximize() {
        GenotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        GenotypeSupport.setPareto(initial);
        assertSame(GenotypeSupport.getPareto(), initial);
        GenotypeSupport.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{3.0, 1.0}));

        GenotypeSupport.addToParetoOptimum(new double[]{5.0, 1.0});
        assertEquals(3, GenotypeSupport.getPareto().size());
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{5.0, 1.0}));
    }

    @Test
    public void addToParetoOptimumTest_basics_withMinimize() {
        GenotypeSupport.setMaximize(false);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        GenotypeSupport.setPareto(initial);
        assertSame(GenotypeSupport.getPareto(), initial);
        GenotypeSupport.addToParetoOptimum(new double[]{5.0, 1.0});
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{3.0, 1.0}));

        GenotypeSupport.addToParetoOptimum(new double[]{1.0, 2.0});
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{3.0, 1.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 2.0}));

        GenotypeSupport.addToParetoOptimum(new double[]{1.0, 1.0});
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 1.0}));
    }

    @Test
    public void addToParetoTest_addUltimateSolution_withMaximize() {
        GenotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        GenotypeSupport.setPareto(initial);
        assertSame(GenotypeSupport.getPareto(), initial);
        GenotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, GenotypeSupport.getPareto().size());
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{5.0, 5.0}));
    }

    @Test
    public void addToParetoTest_addExistingArray() {
        GenotypeSupport.setMaximize(true);
        Set<double[]> initial = new HashSet<>(){{
            add(new double[]{1.0, 3.0});
            add(new double[]{2.0, 2.0});
            add(new double[]{3.0, 1.0});
        }};
        GenotypeSupport.setPareto(initial);
        GenotypeSupport.addToParetoOptimum(new double[]{1.0, 3.0});
        assertEquals(3, GenotypeSupport.getPareto().size());
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{1.0, 3.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{2.0, 2.0}));
        assertTrue(isIn(GenotypeSupport.getPareto(), new double[]{3.0, 1.0}));
        GenotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, GenotypeSupport.getPareto().size());
        GenotypeSupport.addToParetoOptimum(new double[]{5.0, 5.0});
        assertEquals(1, GenotypeSupport.getPareto().size());
    }

    @Test
    public void setDataDirTest() {
        File[] files = new File("src/test/resources/code_files").listFiles();
        if(files!=null) {
            int l = files.length;
            setDataDir(GenotypeSupport.dir_path + "/src/test/resources/code_files");
            File[] dataFiles = new File(GenotypeSupport.dataDir + "generation_0").listFiles();
            assertNotNull(dataFiles);
            assertEquals(l, dataFiles.length);
        } else {
            fail("This directory shouldn't be empty");
        }
    }
}
