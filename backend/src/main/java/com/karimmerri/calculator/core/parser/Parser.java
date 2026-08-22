package com.karimmerri.calculator.core.parser;

import com.karimmerri.calculator.core.ast.BinaryExpression;
import com.karimmerri.calculator.core.ast.Expression;
import com.karimmerri.calculator.core.ast.FunctionExpression;
import com.karimmerri.calculator.core.ast.NumberExpression;
import com.karimmerri.calculator.core.ast.UnaryExpression;
import com.karimmerri.calculator.core.function.FunctionName;
import com.karimmerri.calculator.core.lexer.Token;
import com.karimmerri.calculator.core.lexer.TokenType;
import com.karimmerri.calculator.core.operator.BinaryOperator;
import com.karimmerri.calculator.core.operator.UnaryOperator;

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

            if (!isAtEnd()) {
                throw ParserException.unexpectedToken(peek());
            }

            return expression;
        }

        // expression -> term (("+" | "-") term)*
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

        // term -> unary (("*" | "/") unary)*
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

        // unary -> "-" unary | power
        private Expression unary() {
            if (match(TokenType.MINUS)) {
                return new UnaryExpression(
                        UnaryOperator.NEGATE,
                        unary()
                );
            }

            return power();
        }

        // power -> primary ("^" unary)?
        // Recursive structure makes exponentiation right-associative.
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

        // primary -> NUMBER | "(" expression ")" | IDENTIFIER "(" expression ")"
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

        private Token advance() {
            if (!isAtEnd()) {
                current++;
            }

            return previous();
        }

        private boolean isAtEnd() {
            return peek().type() == TokenType.EOF;
        }

        private Token peek() {
            if (current >= tokens.size()) {
                Token last = tokens.get(tokens.size() - 1);
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
            if (tokens.get(tokens.size() - 1).type() != TokenType.EOF) {
                throw new IllegalArgumentException(
                        "Token stream must terminate with EOF"
                );
            }
        }
    }
}
