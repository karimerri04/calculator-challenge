package com.karimmerri.calculator.core.evaluation;

import com.karimmerri.calculator.core.ast.BinaryExpression;
import com.karimmerri.calculator.core.ast.Expression;
import com.karimmerri.calculator.core.ast.FunctionExpression;
import com.karimmerri.calculator.core.ast.NumberExpression;
import com.karimmerri.calculator.core.ast.UnaryExpression;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExpressionEvaluator {

    private static final BigDecimal ONE = BigDecimal.ONE;

    private final EvaluationContext context;

    public ExpressionEvaluator() {
        this(EvaluationContext.DEFAULT);
    }

    public ExpressionEvaluator(EvaluationContext context) {
        this.context = Objects.requireNonNull(context, "context must not be null");
    }

    public BigDecimal evaluate(Expression expression) {
        Objects.requireNonNull(expression, "expression must not be null");

        return switch (expression) {
            case NumberExpression number -> number.value();
            case UnaryExpression unary -> evaluateUnary(unary);
            case BinaryExpression binary -> evaluateBinary(binary);
            case FunctionExpression function -> evaluateFunction(function);
        };
    }

    private BigDecimal evaluateUnary(UnaryExpression expression) {
        BigDecimal operand = evaluate(expression.operand());

        return switch (expression.operator()) {
            case NEGATE -> operand.negate(context.mathContext());
        };
    }

    private BigDecimal evaluateBinary(BinaryExpression expression) {
        BigDecimal left = evaluate(expression.left());
        BigDecimal right = evaluate(expression.right());

        return switch (expression.operator()) {
            case ADD -> left.add(right, context.mathContext());
            case SUBTRACT -> left.subtract(right, context.mathContext());
            case MULTIPLY -> left.multiply(right, context.mathContext());
            case DIVIDE -> divide(left, right);
            case POWER -> power(left, right);
        };
    }

    private BigDecimal evaluateFunction(FunctionExpression expression) {
        BigDecimal argument = evaluate(expression.argument());

        return switch (expression.function()) {
            case SQRT -> sqrt(argument);
        };
    }

    private BigDecimal divide(BigDecimal left, BigDecimal right) {
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw CalculationException.divisionByZero();
        }

        return left.divide(right, context.mathContext());
    }

    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw CalculationException.negativeSquareRoot();
        }

        return value.sqrt(context.mathContext());
    }

    private BigDecimal power(BigDecimal base, BigDecimal exponent) {
        BigDecimal normalizedExponent = exponent.stripTrailingZeros();

        if (normalizedExponent.scale() > 0) {
            throw CalculationException.nonIntegerExponent();
        }

        final int intExponent;
        try {
            intExponent = normalizedExponent.intValueExact();
        } catch (ArithmeticException exception) {
            throw CalculationException.exponentOutOfRange();
        }

        if (intExponent >= 0) {
            return base.pow(intExponent, context.mathContext());
        }

        if (base.compareTo(BigDecimal.ZERO) == 0) {
            throw CalculationException.divisionByZero();
        }

        int positiveExponent;
        try {
            positiveExponent = Math.negateExact(intExponent);
        } catch (ArithmeticException exception) {
            throw CalculationException.exponentOutOfRange();
        }

        BigDecimal powered = base.pow(
                positiveExponent,
                context.mathContext()
        );

        return ONE.divide(powered, context.mathContext());
    }
}
