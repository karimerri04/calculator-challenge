package com.karimmerri.calculator.core.ast;

import com.karimmerri.calculator.core.function.FunctionName;

import java.util.Objects;

public record FunctionExpression(
        FunctionName function,
        Expression argument
) implements Expression {

    public FunctionExpression {
        Objects.requireNonNull(function, "function must not be null");
        Objects.requireNonNull(argument, "argument must not be null");
    }
}
