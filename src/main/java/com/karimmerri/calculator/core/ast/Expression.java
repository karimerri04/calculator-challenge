package com.karimmerri.calculator.core.ast;

public sealed interface Expression
        permits NumberExpression,
                UnaryExpression,
                BinaryExpression,
                FunctionExpression {
}
