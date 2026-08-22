package com.karimmerri.calculator.core.operator;

public enum UnaryOperator {
    NEGATE("-");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
