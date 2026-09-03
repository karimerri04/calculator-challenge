package com.karimmerri.calculator.core.parser;

import com.karimmerri.calculator.core.lexer.Token;

import java.io.Serial;

public final class ParserException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private ParserException(String message) {
        super(message);
    }

    static ParserException expectedExpression(Token token) {
        return new ParserException(
                "Expected expression at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme())
        );
    }

    static ParserException expectedRightParenthesis(Token token) {
        return new ParserException(
                "Expected ')' at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme())
        );
    }

    static ParserException expectedLeftParenthesis(Token token) {
        return new ParserException(
                "Expected '(' at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme())
        );
    }

    static ParserException unexpectedToken(Token token) {
        return new ParserException(
                "Unexpected token '%s' at position %d"
                        .formatted(token.lexeme(), token.position())
        );
    }

    static ParserException invalidNumber(Token token) {
        return new ParserException(
                "Invalid number '%s' at position %d"
                        .formatted(token.lexeme(), token.position())
        );
    }

    static ParserException unknownFunction(Token token) {
        return new ParserException(
                "Unknown function '%s' at position %d"
                        .formatted(token.lexeme(), token.position())
        );
    }
}
