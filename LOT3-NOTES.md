# Lot 3 — Design Notes

## Evaluation is separate from parsing

The parser answers:

> What does the expression mean structurally?

The evaluator answers:

> What is the numeric result of that structure?

This avoids mixing syntax and arithmetic.

## BigDecimal policy

`BigDecimal` is used for decimal correctness.

Division uses:

```text
MathContext(34, HALF_EVEN)
```

This roughly corresponds to Decimal128 precision and gives deterministic behavior
for non-terminating divisions such as `1 / 3`.

The policy lives in `EvaluationContext`, not hard-coded throughout the evaluator.

## Division by zero

Division by zero is a domain error represented by `CalculationException`.

## Calculator facade

`Calculator` is the application-level entry point:

```text
calculate(String)
   |
   +-> Lexer
   +-> Parser
   `-> Evaluator
```

Consumers do not need to know the internal pipeline.

## Java 21

The project now targets Java 21 LTS for compatibility with common enterprise environments.
No Java 25-only API is required.
