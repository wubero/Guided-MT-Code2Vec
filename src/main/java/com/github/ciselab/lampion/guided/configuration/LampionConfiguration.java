package com.github.ciselab.lampion.guided.configuration;

import com.github.ciselab.lampion.core.program.Engine;
import com.github.ciselab.lampion.core.transformations.Transformer;
import com.github.ciselab.lampion.core.transformations.transformers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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

    /**
     * Gets all available Constructors requiring a seed to make a Transformer.
     * This is hardcoded, for the current used Lampion-Version.
     * @return a List of Constructors, needing a Seed to Create a Transformer
     */
    public List<Function<Long, Transformer>> getAvailableTransformerConstructors(){
        var constructors = new LinkedList<Function<Long, Transformer>>();

        constructors.add(seed -> new IfTrueTransformer(seed));
        constructors.add(seed ->  new IfFalseElseTransformer(seed));
        constructors.add(seed -> new RenameVariableTransformer(seed));
        constructors.add(seed ->  new AddNeutralElementTransformer(seed));
        constructors.add(seed -> new AddUnusedVariableTransformer(seed));
        constructors.add(seed -> new RandomParameterNameTransformer(seed));
        constructors.add(seed -> new LambdaIdentityTransformer(seed));

        return constructors;
    }

}
