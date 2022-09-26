package com.github.ciselab.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class FileManagementTest {

    /*

    @AfterEach
    public void after() {
        var config = new Configuration();
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

    @Tag("File")
    @Test
    public void removeOldDirTest() {
        var config = new Configuration();
        MetricCache cache = new MetricCache();
        GenotypeSupport genotypeSupport = new GenotypeSupport(cache,config);

        File myDir = new File("./code2vec/data/test_1/");
        File currDir = new File("./code2vec/data/" + genotypeSupport.getInitialDataset());
        assertTrue(myDir.mkdir());
        assertTrue(myDir.exists());
        assertTrue(currDir.exists());
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
        assertFalse(myDir.exists());
        assertTrue(currDir.exists());
    }

    TODO: Reimplement
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
            File[] dataFiles = new File(config.program.getDataDirectoryPath() + "generation_0").listFiles();
            assertNotNull(dataFiles);
            assertEquals(l, dataFiles.length);
        } else {
            fail("This directory shouldn't be empty");
        }
    }

    @Tag("File")
    @Test
    public void createDirsTest() {
        var config = new Configuration();

        File f = new File(config.program.getDataDirectoryPath() + "test_dir");
        assertTrue(f.mkdir());
        assertTrue(f.isDirectory());
        assertTrue(FileManagement.createDirs(f.getPath()));
        FileManagement.removeOtherDirs(config.program.getDataDirectoryPath().toString());
    }

     */
}
