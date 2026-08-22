package com.karimmerri.calculator.application;

import com.karimmerri.calculator.core.evaluation.ExpressionEvaluator;
import com.karimmerri.calculator.core.lexer.Lexer;
import com.karimmerri.calculator.core.parser.Parser;

import java.math.BigDecimal;
import java.util.Objects;

public final class Calculator {

    private final Lexer lexer;
    private final Parser parser;
    private final ExpressionEvaluator evaluator;

    public Calculator() {
        this(new Lexer(), new Parser(), new ExpressionEvaluator());
    }

    public Calculator(
            Lexer lexer,
            Parser parser,
            ExpressionEvaluator evaluator
    ) {
        this.lexer = Objects.requireNonNull(lexer, "lexer must not be null");
        this.parser = Objects.requireNonNull(parser, "parser must not be null");
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator must not be null");
    }

    public BigDecimal calculate(String expression) {
        var tokens = lexer.tokenize(expression);
        var ast = parser.parse(tokens);
        return evaluator.evaluate(ast);
    }
}
