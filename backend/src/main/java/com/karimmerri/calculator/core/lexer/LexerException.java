package com.karimmerri.calculator.core.lexer;

import java.io.Serial;

public final class LexerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private LexerException(String message) {
        super(message);
    }

    public static LexerException unexpectedCharacter(char character, int position) {
        return new LexerException(
                "Unexpected character '%s' at position %d"
                        .formatted(character, position)
        );
    }

    public static LexerException malformedNumber(String lexeme, int position) {
        return new LexerException(
                "Malformed number '%s' at position %d"
                        .formatted(lexeme, position)
        );
    }
}
