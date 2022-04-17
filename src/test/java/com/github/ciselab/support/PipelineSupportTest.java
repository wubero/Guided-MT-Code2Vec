package com.github.ciselab.support;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PipelineSupportTest {

    @Test
    public void removeOldDirTest() {
        // set dataset as version test_3
        PipelineSupport.setCurrentDataset("test_3");
        // create test_1 and assert it exists
        File myDir = new File("./code2vec/data/test_1/");
        myDir.mkdir();
        assertTrue(myDir.exists());
        PipelineSupport.removeOldDir();
        assertFalse(myDir.exists());
    }

    @Test
    public void initializeFieldsTest() {
        assertTrue(PipelineSupport.getMetrics().isEmpty());
        PipelineSupport.initializeFields();
        assertFalse(PipelineSupport.getMetrics().isEmpty());
    }
}
