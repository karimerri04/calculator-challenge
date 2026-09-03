package com.karimmerri.calculator.core.expression;

public sealed interface Expression
        permits NumberExpression,
                UnaryExpression,
                BinaryExpression,
                FunctionExpression {
}
