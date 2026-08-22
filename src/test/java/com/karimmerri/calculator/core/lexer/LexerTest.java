package com.karimmerri.calculator.core.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LexerTest {

    private final Lexer lexer = new Lexer();

    @Test
    void shouldTokenizePowerAndFunctionIdentifier() {
        assertThat(typesOf("sqrt(4)^2"))
                .containsExactly(
                        TokenType.IDENTIFIER,
                        TokenType.LEFT_PARENTHESIS,
                        TokenType.NUMBER,
                        TokenType.RIGHT_PARENTHESIS,
                        TokenType.POWER,
                        TokenType.NUMBER,
                        TokenType.EOF
                );
    }

    @Test
    void shouldTokenizeOperatorsAndIgnoreWhitespace() {
        assertThat(typesOf(" 1 + 2 - 3 * 4 / 5 ^ 6 "))
                .containsExactly(
                        TokenType.NUMBER,
                        TokenType.PLUS,
                        TokenType.NUMBER,
                        TokenType.MINUS,
                        TokenType.NUMBER,
                        TokenType.MULTIPLY,
                        TokenType.NUMBER,
                        TokenType.DIVIDE,
                        TokenType.NUMBER,
                        TokenType.POWER,
                        TokenType.NUMBER,
                        TokenType.EOF
                );
    }

    @Test
    void shouldPreserveIdentifierLexeme() {
        assertThat(lexer.tokenize("sqrt(4)").get(0))
                .isEqualTo(new Token(TokenType.IDENTIFIER, "sqrt", 0));
    }

    @Test
    void shouldAcceptStrictDecimal() {
        assertThat(lexer.tokenize("3.14").get(0))
                .isEqualTo(new Token(TokenType.NUMBER, "3.14", 0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"5.", ".5", "1.2.3"})
    void shouldRejectMalformedDecimals(String expression) {
        assertThatThrownBy(() -> lexer.tokenize(expression))
                .isInstanceOf(LexerException.class);
    }

    @Test
    void shouldRejectUnexpectedCharacter() {
        assertThatThrownBy(() -> lexer.tokenize("2 @ 3"))
                .isInstanceOf(LexerException.class)
                .hasMessage("Unexpected character '@' at position 2");
    }

    private List<TokenType> typesOf(String expression) {
        return lexer.tokenize(expression).stream()
                .map(Token::type)
                .toList();
    }
}
