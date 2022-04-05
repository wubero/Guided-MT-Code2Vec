package com.github.ciselab.model;

import com.github.ciselab.metric.*;

public interface Model {

    Metric predict(String method);
}
