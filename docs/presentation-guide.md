# Technical presentation guide

A practical 40–50 minute walkthrough.

## 1. Problem and constraints — 3 to 5 min

Explain the required operators, decimals, negative values, precedence,
parentheses, error handling, and the decision not to use an expression library.

## 2. Architecture — 5 min

Draw the pipeline:

```text
String -> Lexer -> Tokens -> Parser -> AST -> Evaluator -> BigDecimal
```

Then show that Spring exists only around the application/core boundary.

## 3. Lexer — 5 min

Show why unary minus is not part of the number token. Mention strict decimal
rules and source positions for diagnostics.

## 4. Parser and AST — 10 min

Walk through the grammar from `expression()` to `primary()`.
Demonstrate:

- `2 + 2 * 5 + 5 = 17`
- `2^3^2 = 512`
- `-2^2 = -4`
- `(-2)^2 = 4`

Explain why records and a sealed interface fit the closed AST model.

## 5. Evaluation — 5 to 7 min

Explain `BigDecimal`, `MathContext`, division by zero, integer-only powers,
negative exponents, and square-root domain validation.

## 6. REST boundary — 5 min

Show the request/response contract and the four external error codes. Explain
why the response result is serialized as a JSON string.

## 7. Tests, CI, and Docker — 5 min

Run:

```bash
mvn clean verify
```

Show that the same command is used by GitHub Actions and that JaCoCo enforces an
80% line-coverage floor. Then explain the multi-stage Docker image and
`/actuator/health`.

## 8. Trade-offs and next steps — 3 to 5 min

Be ready to state what was intentionally not implemented:

- fractional exponents;
- multi-argument functions;
- arbitrary identifiers/variables;
- a parser-generator library;
- special handling for the contradictory README example `1+1 -> 1`.

Potential next steps should be driven by requirements, not speculative
abstractions.

## Frontend boundary (future lot)

The planned browser UI is an inbound adapter. It sends the expression to the
REST endpoint and displays the returned result or stable API error. Parsing and
calculation remain exclusively in the Java core, avoiding duplicated rules
between Java and TypeScript.
