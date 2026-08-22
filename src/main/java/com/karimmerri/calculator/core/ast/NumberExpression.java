package com.karimmerri.calculator.core.ast;

import java.math.BigDecimal;
import java.util.Objects;

public record NumberExpression(BigDecimal value) implements Expression {

    public NumberExpression {
        Objects.requireNonNull(value, "value must not be null");
    }
}
