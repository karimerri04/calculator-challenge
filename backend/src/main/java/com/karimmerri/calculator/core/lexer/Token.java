package com.karimmerri.calculator.core.lexer;

import java.util.Objects;

public record Token(TokenType type, String lexeme, int position) {

    public Token {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(lexeme, "lexeme must not be null");

        if (position < 0) {
            throw new IllegalArgumentException("position must be >= 0");
        }
    }
}
