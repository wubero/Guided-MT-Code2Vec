package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.ciselab.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    GenotypeSupport genotypeSupport;
    MetricCache metricCache;
    ConfigManagement configManagement;

    @BeforeEach
    public void setUp() {
        metricCache = new MetricCache();
        genotypeSupport = new GenotypeSupport(metricCache);
        configManagement = genotypeSupport.getConfigManagement();
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
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
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
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
}
