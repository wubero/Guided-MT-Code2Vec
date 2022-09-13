package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EditDistanceTest {

    @Tag("File")
    @Test
    public void checkNameTest() {
        EditDistance metric = new EditDistance(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithoutScore.txt");
        assertEquals("EditDistance", metric.getName());
    }

    @Tag("File")
    @Test
    public void calculateScoreTest() {
        EditDistance metric = new EditDistance(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithoutScore.txt");
        assertEquals(0.75, metric.calculateScore());
    }

    @Tag("File")
    @Test
    public void editDistanceTest() {
        EditDistance metric = new EditDistance(GenotypeSupport.dir_path + "/src/test/resources/testPredictionsWithoutScore.txt");
        String[] original = new String[]{"kitten", "sunday", "Lampion", "pre|head"};
        String[] predicted = new String[]{"sitten", "saturday", "Lampion", "pre|head"};
        int[] expectedResults = new int[]{1, 3, 0, 0};
        for(int i = 0; i < original.length; i++) {
            assertEquals(metric.editDistance(original[i], predicted[i]), expectedResults[i]);
        }
    }
}
