# Calculator Challenge — Lot 6

> Reference implementation for study and local practice.
> Do not submit it unchanged if the recruiting challenge prohibits code produced by others.

## Goal

Parse and evaluate mathematical expressions received as strings without using
an external parser/evaluator library.

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

The calculator core remains pure Java and has no Spring annotations.

Detailed documentation:

- `docs/architecture.md`
- `docs/adr/0001-pure-java-core.md`
- `docs/adr/0002-recursive-descent-parser.md`
- `docs/presentation-guide.md`

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

The contradictory README example `1+1 -> 1` is intentionally not implemented
as a special case.

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

## REST API

```http
POST /api/v1/calculations
Content-Type: application/json
```

Request:

```json
{
  "expression": "2 + 2 * 5 + 5"
}
```

Response:

```json
{
  "expression": "2 + 2 * 5 + 5",
  "result": "17"
}
```

### Errors

Stable external error codes:

- `VALIDATION_ERROR`
- `LEXICAL_ERROR`
- `SYNTAX_ERROR`
- `CALCULATION_ERROR`

Example:

```json
{
  "code": "CALCULATION_ERROR",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/calculations"
}
```

## Verify

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

GitHub Actions executes the same `clean verify` command on Java 21.

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

Build:

```bash
docker build -t calculator-challenge:lot6 .
```

Run:

```bash
docker run --rm -p 8080:8080 calculator-challenge:lot6
```

The image is multi-stage and the application runs as a non-root user.
