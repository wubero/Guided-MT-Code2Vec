package com.github.ciselab.support;

import static com.github.ciselab.lampion.guided.support.FileManagement.dataDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.Random;

import com.github.ciselab.lampion.guided.algorithms.RandomAlgorithm;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import com.github.ciselab.lampion.guided.support.ParetoFront;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class FileManagementTest {


    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(dataDir);
    }

    @Tag("File")
    @Test
    public void removeOldDirTest() {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache,config);

        File myDir = new File("./code2vec/data/test_1/");
        File currDir = new File("./code2vec/data/" + genotypeSupport.getCurrentDataset());
        assertTrue(myDir.mkdir());
        assertTrue(myDir.exists());
        assertTrue(currDir.exists());
        FileManagement.removeOtherDirs(dataDir);
        assertFalse(myDir.exists());
        assertTrue(currDir.exists());
    }

    @Tag("File")
    @Test
    public void setDataDirTest() {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache,config);

        File[] files = new File("src/test/resources/code_files").listFiles();
        if(files!=null) {
            int l = files.length;
            FileManagement.setDataDir(config.program.getDataDirectoryPath() + "/src/test/resources/code_files");
            File[] dataFiles = new File(dataDir + "generation_0").listFiles();
            assertNotNull(dataFiles);
            assertEquals(l, dataFiles.length);
        } else {
            fail("This directory shouldn't be empty");
        }
    }

    @Tag("File")
    @Test
    public void createDirsTest() {
        File f = new File(dataDir + "test_dir");
        assertTrue(f.mkdir());
        assertTrue(f.isDirectory());
        assertTrue(FileManagement.createDirs(f.getPath()));
        FileManagement.removeOtherDirs(dataDir);
    }
}
