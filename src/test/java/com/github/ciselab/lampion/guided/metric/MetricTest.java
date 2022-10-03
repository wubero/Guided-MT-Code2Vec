package com.github.ciselab.lampion.guided.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricTest {

    /*TODO: Reimplement
    @Tag("File")
    @Test
    public void readPredictionsTest() {
        F1_score m = new F1_score(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals(m.getName(), "F1Score");
        File f = new File(m.getPath());
        assertTrue(f.exists());
        List<String> predictions = m.readPredictions(m.getPath());
        assertTrue(predictions.size() > 1);
        assertEquals(predictions.get(0), "Original: inc|level, predicted 1st: inc|level, score: 27");
    }

    @Tag("File")
    @Test
    public void readPredictionsTest_withWrongPath() {
        F1_score m = new F1_score(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithScore.txt");
        assertEquals(m.getName(), "F1Score");
        m.setPath("/");
        assertEquals(0, m.readPredictions(m.getPath()).size());
    }

     */
}
