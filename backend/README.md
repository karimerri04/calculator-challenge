# Calculator Challenge — Backend

## Goal

Parse and evaluate mathematical expressions received as strings without using
an external parser or expression-evaluation library.

## Architecture

```text
HTTP
  |
  v
REST adapter — Spring MVC
  |
  v
Calculator application facade
  |
  +-> Lexer -------> List<Token>
  +-> Parser ------> AST
  `-> Evaluator ---> BigDecimal
```

The calculator core is pure Java and contains no Spring annotations.

Documentation:

- `../docs/architecture.md`
- `../docs/testing-strategy.md`
- `../docs/api-contract.md`
- `../docs/frontend-plan.md`
- `../docs/presentation-guide.md`
- `../docs/adr/0001-pure-java-core.md`
- `../docs/adr/0002-recursive-descent-parser.md`
- `../docs/adr/0003-bigdecimal.md`

## Stack

- Java 21 LTS
- Spring Boot 4.1.1
- Spring MVC
- Spring Boot Actuator
- Jakarta Validation
- Maven
- JUnit / AssertJ / MockMvc
- JaCoCo
- BigDecimal
- Docker
- GitHub Actions
- React 19 / TypeScript / Vite
- Vitest

## Supported expressions

```text
1 + 2            -> 3
1 + -1           -> 0
-1 - -1          -> 0
5 - 4            -> 1
5 * 2            -> 10
(2 + 5) * 3      -> 21
10 / 2           -> 5
2 + 2 * 5 + 5    -> 17
2.8 * 3 - 1      -> 7.4
2^8              -> 256
2^8*5-1          -> 1279
2^3^2            -> 512
-2^2             -> -4
(-2)^2           -> 4
2^-2             -> 0.25
sqrt(4)          -> 2
1/0              -> error
```

The contradictory challenge example `1+1 -> 1` is intentionally not
implemented as a special case.

### Deliberate limits

- decimal syntax is strict: `3.14` is valid, while `5.`, `.5` and `1.2.3` are rejected;
- only integer exponents are supported;
- integer exponents are limited to the range `[-10000, 10000]`;
- square root of a negative number is rejected;
- division by zero is rejected.

## Grammar

```text
expression -> term (("+" | "-") term)*
term       -> unary (("*" | "/") unary)*
unary      -> "-" unary | power
power      -> primary ("^" unary)?
primary    -> NUMBER
           | "(" expression ")"
           | IDENTIFIER "(" expression ")"
```

This intentionally gives:

```text
2^3^2   -> 512
-2^2    -> -4
(-2)^2  -> 4
```

## REST API

```http
POST /api/v1/calculations
Content-Type: application/json
```

Request:

```json
{
  "expression": "sqrt(4) + 2^3"
}
```

Response:

```json
{
  "expression": "sqrt(4) + 2^3",
  "result": "10"
}
```

Error codes:

- `VALIDATION_ERROR`
- `LEXICAL_ERROR`
- `SYNTAX_ERROR`
- `CALCULATION_ERROR`

See `../docs/api-contract.md` for the complete boundary contract.

## Test and verify

Canonical command:

```bash
mvn clean verify
```

This runs the test suite, generates JaCoCo coverage, and enforces an 80% line
coverage floor.

Coverage report:

```text
target/site/jacoco/index.html
```

The test strategy covers individual primitives, combined operators, longer
expressions and nested structures. See `../docs/testing-strategy.md`.

## Run locally

```bash
mvn spring-boot:run
```

Calculator endpoint:

```text
POST http://localhost:8080/api/v1/calculations
```

Health endpoint:

```text
GET http://localhost:8080/actuator/health
```

## Docker

```bash
docker build -t calculator-challenge .
docker run --rm -p 8080:8080 calculator-challenge
```

The image is multi-stage and the application runs as a non-root user.
