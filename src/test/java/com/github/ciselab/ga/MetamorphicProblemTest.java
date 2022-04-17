package com.github.ciselab.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MetamorphicProblemTest {

    @Test
    public void createTransformerTest() {
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.RenameVariableTransformer", MetamorphicProblem.createTransformers(3, 1).getClass().toString());
    }
}
