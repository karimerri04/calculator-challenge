package com.karimmerri.calculator.core.parser;

import com.karimmerri.calculator.core.expression.BinaryExpression;
import com.karimmerri.calculator.core.expression.BinaryOperator;
import com.karimmerri.calculator.core.expression.Expression;
import com.karimmerri.calculator.core.expression.FunctionExpression;
import com.karimmerri.calculator.core.expression.FunctionName;
import com.karimmerri.calculator.core.expression.NumberExpression;
import com.karimmerri.calculator.core.expression.UnaryExpression;
import com.karimmerri.calculator.core.expression.UnaryOperator;
import com.karimmerri.calculator.core.lexer.Token;
import com.karimmerri.calculator.core.lexer.TokenType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class Parser {

    public Expression parse(List<Token> tokens) {
        Objects.requireNonNull(tokens, "tokens must not be null");

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("tokens must not be empty");
        }

        // Parsing state belongs to one call, keeping the shared Parser stateless.
        return new ParseSession(List.copyOf(tokens)).parse();
    }

    private static final class ParseSession {

        private final List<Token> tokens;
        private int current;

        private ParseSession(List<Token> tokens) {
            this.tokens = tokens;
        }

        private Expression parse() {
            ensureEofExists();

            Expression expression = expression();

            if (hasRemainingTokens()) {
                throw ParserException.unexpectedToken(peek());
            }

            return expression;
        }

        /*
         * Grammar, from lowest to highest precedence:
         *
         * expression -> term (("+" | "-") term)*
         * term       -> unary (("*" | "/") unary)*
         * unary      -> "-" unary | power
         * power      -> primary ("^" unary)?
         * primary    -> NUMBER
         *             | "(" expression ")"
         *             | IDENTIFIER "(" expression ")"
         */

        private Expression expression() {
            Expression left = term();

            while (match(TokenType.PLUS, TokenType.MINUS)) {
                Token operator = previous();
                Expression right = term();

                left = new BinaryExpression(
                        left,
                        toAdditiveOperator(operator),
                        right
                );
            }

            return left;
        }

        private Expression term() {
            Expression left = unary();

            while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
                Token operator = previous();
                Expression right = unary();

                left = new BinaryExpression(
                        left,
                        toMultiplicativeOperator(operator),
                        right
                );
            }

            return left;
        }

        private Expression unary() {
            if (match(TokenType.MINUS)) {
                return new UnaryExpression(
                        UnaryOperator.NEGATE,
                        unary()
                );
            }

            return power();
        }

        /*
         * Recursion on the right makes exponentiation right-associative:
         * 2^3^2 -> 2^(3^2), while power still binds before unary minus.
         */
        private Expression power() {
            Expression left = primary();

            if (match(TokenType.POWER)) {
                Expression right = unary();

                return new BinaryExpression(
                        left,
                        BinaryOperator.POWER,
                        right
                );
            }

            return left;
        }

        private Expression primary() {
            if (match(TokenType.NUMBER)) {
                Token number = previous();

                try {
                    return new NumberExpression(new BigDecimal(number.lexeme()));
                } catch (NumberFormatException exception) {
                    throw ParserException.invalidNumber(number);
                }
            }

            if (match(TokenType.LEFT_PARENTHESIS)) {
                Expression nested = expression();
                consume(
                        TokenType.RIGHT_PARENTHESIS,
                        ParserException::expectedRightParenthesis
                );
                return nested;
            }

            if (match(TokenType.IDENTIFIER)) {
                return function(previous());
            }

            throw ParserException.expectedExpression(peek());
        }

        private Expression function(Token identifier) {
            FunctionName functionName = FunctionName
                    .fromIdentifier(identifier.lexeme())
                    .orElseThrow(() -> ParserException.unknownFunction(identifier));

            consume(
                    TokenType.LEFT_PARENTHESIS,
                    ParserException::expectedLeftParenthesis
            );

            Expression argument = expression();

            consume(
                    TokenType.RIGHT_PARENTHESIS,
                    ParserException::expectedRightParenthesis
            );

            return new FunctionExpression(functionName, argument);
        }

        private BinaryOperator toAdditiveOperator(Token token) {
            return switch (token.type()) {
                case PLUS -> BinaryOperator.ADD;
                case MINUS -> BinaryOperator.SUBTRACT;
                default -> throw new IllegalStateException(
                        "Not an additive operator: " + token.type()
                );
            };
        }

        private BinaryOperator toMultiplicativeOperator(Token token) {
            return switch (token.type()) {
                case MULTIPLY -> BinaryOperator.MULTIPLY;
                case DIVIDE -> BinaryOperator.DIVIDE;
                default -> throw new IllegalStateException(
                        "Not a multiplicative operator: " + token.type()
                );
            };
        }

        private boolean match(TokenType... types) {
            for (TokenType type : types) {
                if (check(type)) {
                    advance();
                    return true;
                }
            }

            return false;
        }

        private boolean check(TokenType type) {
            return peek().type() == type;
        }

        private void advance() {
            if (hasRemainingTokens()) {
                current++;
            }
        }

        private boolean hasRemainingTokens() {
            return peek().type() != TokenType.EOF;
        }

        private Token peek() {
            if (current >= tokens.size()) {
                Token last = tokens.getLast();
                throw new IllegalArgumentException(
                        "Token stream must terminate with EOF; last token was "
                                + last.type()
                );
            }

            return tokens.get(current);
        }

        private Token previous() {
            return tokens.get(current - 1);
        }

        private void consume(
                TokenType expected,
                Function<Token, ParserException> exceptionFactory
        ) {
            if (check(expected)) {
                advance();
                return;
            }

            throw exceptionFactory.apply(peek());
        }

        private void ensureEofExists() {
            if (tokens.getLast().type() != TokenType.EOF) {
                throw new IllegalArgumentException(
                        "Token stream must terminate with EOF"
                );
            }
        }
    }
}
