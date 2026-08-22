package com.karimmerri.calculator.core.lexer;

public final class LexerException extends RuntimeException {

    private final int position;

    public LexerException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int position() {
        return position;
    }

    public static LexerException unexpectedCharacter(char character, int position) {
        return new LexerException(
                "Unexpected character '%s' at position %d".formatted(character, position),
                position
        );
    }

    public static LexerException malformedNumber(String lexeme, int position) {
        return new LexerException(
                "Malformed number '%s' at position %d".formatted(lexeme, position),
                position
        );
    }
}
