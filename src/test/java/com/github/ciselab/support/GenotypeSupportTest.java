package com.github.ciselab.support;

import com.github.ciselab.lampion.core.transformations.Transformer;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenotypeSupportTest {

    @AfterEach
    public void after() {

        var config = new Configuration();
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runTransformationsTest() throws IOException {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        var support =  new GenotypeSupport(cache,config);

        var testObject = new MetamorphicIndividual(support, 0);
        List<Transformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(config.program.getDataDirectoryPath() + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = support.runTransformations(testObject, "code_files");

        assertTrue(cache.getDir(transformers).isPresent());
        assertEquals(cache.getDir(transformers).get(), name);
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void runCode2vecInferenceTest() throws IOException {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        var support =  new GenotypeSupport(cache,config);

        var testObject = new MetamorphicIndividual(support, 0);

        List<BaseTransformer> transformers = new ArrayList<>();
        File[] files = new File("src/test/resources/code_files").listFiles();
        File directory = new File(config.program.getDataDirectoryPath() + "code_files");
        if(!directory.exists())
            directory.mkdir();
        if(files!=null) {
            for(File file: files) {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(directory.getAbsolutePath() + "/" + file.getName()));
            }
        }
        String name = support.runTransformations(testObject, "code_files");
        support.runCode2vec(name,name+"/results/");

        assertTrue(support.getTotalCode2vevTime() > 0);
    }
}
