package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.metric.Metric;
import org.junit.jupiter.api.Test;

public class EditDistanceTest {

    @Test
    public void editDistanceTest() {
        EditDistance metric = new EditDistance();
        String[] original = new String[]{"kitten", "sunday", "Lampion", "pre|head"};
        String[] predicted = new String[]{"sitten", "saturday", "Lampion", "pre|head"};
        int[] expectedResults = new int[]{1, 3, 0, 0};
        for(int i = 0; i < original.length; i++) {
            assertEquals(metric.editDistance(original[i], predicted[i]), expectedResults[i]);
        }
    }
}
