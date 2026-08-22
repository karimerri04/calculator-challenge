package com.karimmerri.calculator.core.evaluation;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public record EvaluationContext(MathContext mathContext) {

    public static final EvaluationContext DEFAULT =
            new EvaluationContext(new MathContext(34, RoundingMode.HALF_EVEN));

    public EvaluationContext {
        Objects.requireNonNull(mathContext, "mathContext must not be null");
    }
}
