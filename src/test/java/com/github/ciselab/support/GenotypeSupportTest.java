package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
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

import static com.github.ciselab.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    GenotypeSupport genotypeSupport;
    MetricCache metricCache;
    ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        metricCache = new MetricCache();
        genotypeSupport = new GenotypeSupport(metricCache);
        configManager = genotypeSupport.getConfigManager();
        configManager.setConfigFile("src/test/resources/config.properties");
        configManager.initializeFields();
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runTransformationsTest() throws IOException {
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(dataDir + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = genotypeSupport.runTransformations(transformers, "code_files");
        assertTrue(metricCache.getDir(transformers).isPresent());
        assertEquals(metricCache.getDir(transformers).get(), name);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runCode2vecInferenceTest() throws IOException {
        configManager.setConfigFile("src/test/resources/config.properties");
        configManager.initializeFields();
        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(dataDir + "code_files");
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
        configManager.setMaximize(true);
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
        configManager.setMaximize(false);
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
        configManager.setMaximize(true);
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
        configManager.setMaximize(true);
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
}
