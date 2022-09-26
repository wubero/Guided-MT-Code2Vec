package com.github.ciselab.lampion.guided.configuration;

import com.github.ciselab.lampion.core.program.Engine;

public class LampionConfiguration {

    private boolean removeAllComments = false;
    private Engine.TransformationScope transformationScope = Engine.TransformationScope.perClass;

    private int maxTransformerValue = 6; // Including 0, so 7 transformers

    public boolean isRemoveAllComments() {
        return removeAllComments;
    }

    public void setRemoveAllComments(boolean removeAllComments) {
        this.removeAllComments = removeAllComments;
    }

    public Engine.TransformationScope getTransformationScope() {
        return transformationScope;
    }

    public void setTransformationScope(Engine.TransformationScope transformationScope) {
        this.transformationScope = transformationScope;
    }

    public int getMaxTransformerValue() {
        return maxTransformerValue;
    }

    public void setMaxTransformerValue(int maxTransformerValue) {
        this.maxTransformerValue = maxTransformerValue;
    }
}
