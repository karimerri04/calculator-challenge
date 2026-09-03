# Calculator Challenge

A small full-stack calculator that evaluates mathematical expressions supplied as strings.

The implementation deliberately separates lexical analysis, parsing and evaluation instead of relying on an expression-evaluation library. The goal is to keep the calculation rules explicit, testable and independent from the web framework.

## Architecture

```text
React / TypeScript
       |
       | POST /api/v1/calculations
       v
Spring MVC adapter
       |
       v
Calculator (application facade)
       |
       v
String -> Lexer -> Tokens -> Parser -> AST -> ExpressionEvaluator -> BigDecimal
```

The main dependency rule is:

```text
REST / Spring -> application -> core
```

The calculation core is plain Java. Spring is used at the application boundary for HTTP, validation and dependency wiring.

### Core responsibilities

```text
core/
├── lexer/       String -> List<Token>
├── parser/      List<Token> -> Expression AST
├── expression/  Immutable AST model and supported operations
└── evaluation/  Expression AST -> BigDecimal
```

`Parser` is stateless at component level. The mutable cursor required while parsing is isolated in a per-call `ParseSession`, so concurrent calculations do not share parser state.

The AST is represented by a sealed `Expression` hierarchy using records. This gives the evaluator an exhaustive set of expression types:

- `NumberExpression`
- `UnaryExpression`
- `BinaryExpression`
- `FunctionExpression`

## Grammar

The parser is a hand-written recursive-descent parser:

```text
expression -> term (("+" | "-") term)*
term       -> unary (("*" | "/") unary)*
unary      -> "-" unary | power
power      -> primary ("^" unary)?
primary    -> NUMBER
           | "(" expression ")"
           | IDENTIFIER "(" expression ")"
```

This structure encodes precedence directly in the parser. Exponentiation is right-associative, so `2^3^2` is interpreted as `2^(3^2)`. Power also binds before unary minus, so `-2^2` evaluates to `-4`, while `(-2)^2` evaluates to `4`.

## Numeric model

Calculations use `BigDecimal` rather than binary floating-point arithmetic.

The default evaluation context is:

```java
new MathContext(34, RoundingMode.HALF_EVEN)
```

This provides 34 significant digits of precision and a deterministic rounding policy for operations that require rounding.

Supported operations include:

```text
+  -  *  /  ^  sqrt(...)
```

Integer positive and negative exponents are supported. Fractional exponents are intentionally rejected. Division by zero and square roots of negative numbers are reported as calculation errors.

## Examples

```text
1 + 2            -> 3
1 + -1           -> 0
-1 - -1          -> 0
(2 + 5) * 3      -> 21
2 + 2 * 5 + 5    -> 17
2.8 * 3 - 1      -> 7.4
2^8              -> 256
2^3^2            -> 512
-2^2             -> -4
(-2)^2            -> 4
2^-2             -> 0.25
sqrt(4)          -> 2
1/0              -> error
```

### Requirement ambiguity

The supplied challenge examples contain `1+1 -> 1`, while the other examples describe conventional arithmetic. This implementation treats that case as an inconsistent example and returns `2`. In a client project, I would confirm the intended behavior before implementing a special rule.

## REST API

### Calculate

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

Success:

```json
{
  "expression": "2 + 2 * 5 + 5",
  "result": "17"
}
```

The result is serialized as a string so the textual `BigDecimal` representation is preserved at the API boundary.

Handled validation and calculation failures return HTTP `400` with a stable error contract:

```json
{
  "code": "SYNTAX_ERROR",
  "message": "Expected expression at position 3 but found ''",
  "path": "/api/v1/calculations"
}
```

Error categories:

```text
VALIDATION_ERROR
LEXICAL_ERROR
SYNTAX_ERROR
CALCULATION_ERROR
```

## Frontend

The React client is intentionally thin. It:

- sends the raw expression to the backend;
- displays the returned result or API error;
- provides expression shortcuts and examples;
- keeps the last eight successful calculations in browser `localStorage`.

Parsing and arithmetic are not duplicated in TypeScript. The Java backend remains the source of truth.

## Run locally

### Backend

Requirements: Java 21 and Maven.

```bash
cd backend
mvn clean verify
mvn spring-boot:run
```

API:

```text
http://localhost:8080/api/v1/calculations
```

Health check:

```text
http://localhost:8080/actuator/health
```

### Frontend

Requirements: Node.js 22+.

```bash
cd frontend
npm ci
npm test
npm run build
npm run dev
```

Open:

```text
http://localhost:5173
```

During development, Vite proxies `/api` to `http://localhost:8080`.

## Verification

Backend:

```bash
cd backend
mvn clean verify
```

The Maven verification phase runs the test suite, generates a JaCoCo report and enforces an 80% line-coverage floor.

Frontend:

```bash
cd frontend
npm test
npm run build
```

GitHub Actions runs backend and frontend verification independently and also builds the backend Docker image.

## Docker

```bash
docker build -t calculator-challenge-backend backend
docker run --rm -p 8080:8080 calculator-challenge-backend
```

The image uses a multi-stage build, runs the application as a non-root user and exposes an Actuator-based health check.

## Design choices and trade-offs

- **Pure Java core:** calculation rules do not depend on Spring.
- **Recursive-descent parser:** small grammar, explicit precedence, no parser dependency.
- **Sealed AST + records:** closed, immutable expression model that works naturally with Java 21 pattern matching.
- **`BigDecimal`:** decimal arithmetic with an explicit precision and rounding policy.
- **External Spring wiring:** core and application classes remain ordinary Java objects.
- **Stable API errors:** transport validation, lexical errors, syntax errors and calculation errors remain distinguishable.
- **Thin frontend:** no duplicated parser or arithmetic rules.

Given more time, I would first clarify additional domain requirements before expanding the grammar—for example variables, multi-argument functions or fractional powers—rather than adding abstractions without a concrete need.
