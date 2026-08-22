package com.karimmerri.calculator.config;

import com.karimmerri.calculator.application.Calculator;
import com.karimmerri.calculator.core.evaluation.ExpressionEvaluator;
import com.karimmerri.calculator.core.lexer.Lexer;
import com.karimmerri.calculator.core.parser.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculatorConfiguration {

    @Bean
    Lexer lexer() {
        return new Lexer();
    }

    @Bean
    Parser parser() {
        return new Parser();
    }

    @Bean
    ExpressionEvaluator expressionEvaluator() {
        return new ExpressionEvaluator();
    }

    @Bean
    Calculator calculator(
            Lexer lexer,
            Parser parser,
            ExpressionEvaluator evaluator
    ) {
        return new Calculator(lexer, parser, evaluator);
    }
}
