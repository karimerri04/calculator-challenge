# Testing Strategy

## Objective

The test suite protects the observable calculator contract while keeping tests
close to the responsibility of each component.

## Test layers

### Lexer tests

Validate lexical rules independently from parsing:

- operators and parentheses;
- identifiers;
- whitespace handling;
- strict decimal syntax;
- unexpected characters.

### Parser tests

Validate grammar and AST shape independently from evaluation:

- operator precedence;
- right-associative exponentiation;
- unary minus precedence;
- nested parentheses;
- function syntax;
- syntax failures.

### Evaluator tests

Construct AST nodes directly and validate arithmetic semantics:

- addition, subtraction, multiplication and division;
- integer powers, including negative exponents;
- `sqrt`;
- domain failures and exponent guard rails.

### Application tests

Exercise the complete pure-Java pipeline:

```text
String -> Lexer -> Parser -> AST -> Evaluator -> BigDecimal
```

These tests include the examples from the challenge plus combined and nested
expressions.

### REST tests

MockMvc verifies the HTTP boundary:

- successful calculations;
- request validation;
- stable error codes;
- mapping lexical, syntax and calculation failures to HTTP 400.

## Incremental complexity

Tests intentionally progress from individual primitives to combinations and
then to deeper expressions. Three useful dimensions are covered:

1. **Length** — longer sequences such as `1+2+...+10`.
2. **Combination** — precedence, unary operators, powers and functions.
3. **Depth** — nested parentheses and nested expression structures.

This approach gives useful boundary coverage without introducing property-based
or fuzzing frameworks that would be excessive for the challenge.

## Quality gate

`mvn clean verify` is the canonical command. JaCoCo enforces a minimum 80% line
coverage at bundle level. Coverage is a guard rail, not the objective: tests
must assert behavior and meaningful failure modes.
