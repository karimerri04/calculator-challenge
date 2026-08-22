package com.karimmerri.calculator.core.parser;

import com.karimmerri.calculator.core.lexer.Token;

public final class ParserException extends RuntimeException {

    private final int position;

    public ParserException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int position() {
        return position;
    }

    static ParserException expectedExpression(Token token) {
        return new ParserException(
                "Expected expression at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme()),
                token.position()
        );
    }

    static ParserException expectedRightParenthesis(Token token) {
        return new ParserException(
                "Expected ')' at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme()),
                token.position()
        );
    }

    static ParserException expectedLeftParenthesis(Token token) {
        return new ParserException(
                "Expected '(' at position %d but found '%s'"
                        .formatted(token.position(), token.lexeme()),
                token.position()
        );
    }

    static ParserException unexpectedToken(Token token) {
        return new ParserException(
                "Unexpected token '%s' at position %d"
                        .formatted(token.lexeme(), token.position()),
                token.position()
        );
    }

    static ParserException invalidNumber(Token token) {
        return new ParserException(
                "Invalid number '%s' at position %d"
                        .formatted(token.lexeme(), token.position()),
                token.position()
        );
    }

    static ParserException unknownFunction(Token token) {
        return new ParserException(
                "Unknown function '%s' at position %d"
                        .formatted(token.lexeme(), token.position()),
                token.position()
        );
    }
}
