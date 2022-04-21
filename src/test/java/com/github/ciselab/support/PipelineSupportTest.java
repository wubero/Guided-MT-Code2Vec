package com.github.ciselab.support;

import java.io.File;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PipelineSupportTest {

    @Test
    public void removeOldDirTest() {
        // create test_1 and assert it exists
        File myDir = new File("./code2vec/data/test_1/");
        myDir.mkdir();
        assertTrue(myDir.exists());
        GenotypeSupport.removeOtherDirs();
        assertFalse(myDir.exists());
    }

    @Test
    public void initializeFieldsTest() {
        assertTrue(GenotypeSupport.getMetrics().isEmpty());
        GenotypeSupport.initializeFields();
        assertFalse(GenotypeSupport.getMetrics().isEmpty());
    }
}
