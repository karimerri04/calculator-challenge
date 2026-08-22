package com.karimmerri.calculator.core.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Lexer {

    public List<Token> tokenize(String expression) {
        Objects.requireNonNull(expression, "expression must not be null");

        var tokens = new ArrayList<Token>();
        int current = 0;

        while (current < expression.length()) {
            char currentChar = expression.charAt(current);

            if (Character.isWhitespace(currentChar)) {
                current++;
                continue;
            }

            if (Character.isDigit(currentChar)) {
                current = readNumber(expression, current, tokens);
                continue;
            }

            if (Character.isLetter(currentChar)) {
                current = readIdentifier(expression, current, tokens);
                continue;
            }

            tokens.add(symbolToken(currentChar, current));
            current++;
        }

        tokens.add(new Token(TokenType.EOF, "", expression.length()));
        return List.copyOf(tokens);
    }

    private int readNumber(String expression, int start, List<Token> tokens) {
        int current = start;

        while (current < expression.length() && Character.isDigit(expression.charAt(current))) {
            current++;
        }

        if (current < expression.length() && expression.charAt(current) == '.') {
            int decimalPointPosition = current;
            current++;

            if (current >= expression.length() || !Character.isDigit(expression.charAt(current))) {
                String malformed = expression.substring(start, current);
                throw LexerException.malformedNumber(malformed, decimalPointPosition);
            }

            while (current < expression.length() && Character.isDigit(expression.charAt(current))) {
                current++;
            }

            if (current < expression.length() && expression.charAt(current) == '.') {
                int secondDecimalPoint = current;
                int malformedEnd = current + 1;

                while (malformedEnd < expression.length()
                        && (Character.isDigit(expression.charAt(malformedEnd))
                        || expression.charAt(malformedEnd) == '.')) {
                    malformedEnd++;
                }

                String malformed = expression.substring(start, malformedEnd);
                throw LexerException.malformedNumber(malformed, secondDecimalPoint);
            }
        }

        tokens.add(new Token(
                TokenType.NUMBER,
                expression.substring(start, current),
                start
        ));

        return current;
    }

    private int readIdentifier(String expression, int start, List<Token> tokens) {
        int current = start;

        while (current < expression.length()
                && Character.isLetter(expression.charAt(current))) {
            current++;
        }

        tokens.add(new Token(
                TokenType.IDENTIFIER,
                expression.substring(start, current),
                start
        ));

        return current;
    }

    private Token symbolToken(char character, int position) {
        return switch (character) {
            case '+' -> new Token(TokenType.PLUS, "+", position);
            case '-' -> new Token(TokenType.MINUS, "-", position);
            case '*' -> new Token(TokenType.MULTIPLY, "*", position);
            case '/' -> new Token(TokenType.DIVIDE, "/", position);
            case '^' -> new Token(TokenType.POWER, "^", position);
            case '(' -> new Token(TokenType.LEFT_PARENTHESIS, "(", position);
            case ')' -> new Token(TokenType.RIGHT_PARENTHESIS, ")", position);
            default -> throw LexerException.unexpectedCharacter(character, position);
        };
    }
}
