# Lot 5 — REST Adapter Design Notes

## Architecture

```text
HTTP
 |
 v
CalculationController
 |
 v
Calculator
 |
 +-> Lexer
 +-> Parser
 `-> ExpressionEvaluator
```

Spring exists only outside the core.

## Modern Spring Boot 4.1 choice

This lot uses:

```text
spring-boot-starter-webmvc
```

instead of the older `spring-boot-starter-web` starter.

## DTO records

- `CalculationRequest`
- `CalculationResponse`
- `ApiError`

These are immutable transport values.

## Validation

The API boundary enforces:

- non-blank expression
- maximum 1000 characters

## Stable HTTP error contract

- `VALIDATION_ERROR`
- `LEXICAL_ERROR`
- `SYNTAX_ERROR`
- `CALCULATION_ERROR`

All user-expression errors currently map to HTTP 400.

## Why result is JSON String

A calculator should not force its precise decimal result through a binary
floating-point JSON interpretation.

Example:

```json
{
  "result": "7.4"
}
```

## Why explicit Spring configuration

The pure Java core contains no Spring stereotype annotations.

`CalculatorConfiguration` wires the application only at the framework edge.
