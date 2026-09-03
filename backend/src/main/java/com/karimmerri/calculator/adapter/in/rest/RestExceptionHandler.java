package com.karimmerri.calculator.adapter.in.rest;

import com.karimmerri.calculator.core.evaluation.CalculationException;
import com.karimmerri.calculator.core.lexer.LexerException;
import com.karimmerri.calculator.core.parser.ParserException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Spring MVC invokes the @ExceptionHandler methods through framework dispatch.
@SuppressWarnings("unused")
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(LexerException.class)
    ResponseEntity<ApiError> handleLexer(
            LexerException exception,
            HttpServletRequest request
    ) {
        return badRequest("LEXICAL_ERROR", exception.getMessage(), request);
    }

    @ExceptionHandler(ParserException.class)
    ResponseEntity<ApiError> handleParser(
            ParserException exception,
            HttpServletRequest request
    ) {
        return badRequest("SYNTAX_ERROR", exception.getMessage(), request);
    }

    @ExceptionHandler(CalculationException.class)
    ResponseEntity<ApiError> handleCalculation(
            CalculationException exception,
            HttpServletRequest request
    ) {
        return badRequest("CALCULATION_ERROR", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Invalid request");

        return badRequest("VALIDATION_ERROR", message, request);
    }

    private ResponseEntity<ApiError> badRequest(
            String code,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        code,
                        message,
                        request.getRequestURI()
                ));
    }
}
