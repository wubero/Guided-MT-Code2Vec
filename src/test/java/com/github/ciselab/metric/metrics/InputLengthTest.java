package com.github.ciselab.metric.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.ciselab.lampion.guided.metric.metrics.InputLength;
import org.junit.jupiter.api.Test;

public class InputLengthTest {

    @Test
    public void checkNameTest() {
        InputLength metric = new InputLength();
        assertEquals("InputLength", metric.getName());
    }

    //TODO: Reimplement
}
