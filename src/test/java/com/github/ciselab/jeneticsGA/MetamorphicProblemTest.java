package com.github.ciselab.jeneticsGA;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MetamorphicProblemTest {

    @Test
    public void createSpecificTransformerTest() {
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer", MetamorphicProblem.createTransformers(0, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.IfFalseElseTransformer", MetamorphicProblem.createTransformers(1, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.RenameVariableTransformer", MetamorphicProblem.createTransformers(2, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer", MetamorphicProblem.createTransformers(3, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.AddUnusedVariableTransformer", MetamorphicProblem.createTransformers(4, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.LambdaIdentityTransformer", MetamorphicProblem.createTransformers(5, 1).getClass().toString());
        assertEquals("class com.github.ciselab.lampion.core.transformations.transformers.RandomParameterNameTransformer", MetamorphicProblem.createTransformers(6, 1).getClass().toString());
    }
}
