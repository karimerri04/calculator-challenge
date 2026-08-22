# Architecture

## Goal

Keep the calculator rules independent from Spring while exposing the application
through a small HTTP adapter.

## Runtime flow

```text
HTTP POST /api/v1/calculations
        |
        v
CalculationController
        |
        v
Calculator
        |
        +--> Lexer --------> List<Token>
        |
        +--> Parser -------> AST
        |
        `--> Evaluator ----> BigDecimal
```

## Package responsibilities

```text
com.karimmerri.calculator
├── application     use-case facade
├── core
│   ├── lexer       characters -> tokens
│   ├── parser      tokens -> AST
│   ├── ast         immutable expression model
│   ├── operator    supported operators
│   ├── function    supported functions
│   └── evaluation  AST -> BigDecimal
├── config          Spring wiring only
└── adapter/in/rest HTTP transport and error mapping
```

## Dependency direction

The important dependency rule is:

```text
REST / Spring ---> application ---> core
```

The core does not depend on Spring MVC, validation annotations, HTTP types, or
Spring stereotypes. This allows parser and evaluator tests to run as plain Java
unit tests.

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

Consequences:

- multiplication/division bind more strongly than addition/subtraction;
- exponentiation is right-associative;
- exponentiation binds more strongly than unary minus;
- `2^3^2` is `2^(3^2)`;
- `-2^2` is `-(2^2)`;
- `(-2)^2` keeps the negative value inside the base.

## Numeric model

`BigDecimal` is used instead of `double` so decimal inputs such as `2.8` are
not routed through binary floating-point arithmetic.

Evaluation uses a `MathContext` of 34 digits with `HALF_EVEN` rounding.
Division by zero and unsupported mathematical domains are converted to domain
exceptions rather than leaking low-level arithmetic exceptions through HTTP.

## API boundary

The REST layer owns:

- request validation;
- JSON DTOs;
- HTTP status codes;
- stable external error codes.

The core owns mathematical syntax and evaluation rules.

## Verification pipeline

```text
mvn clean verify
  |
  +--> compile
  +--> unit and MVC tests
  +--> JaCoCo report
  `--> 80% line-coverage gate
```

The HTML coverage report is generated under:

```text
target/site/jacoco/index.html
```

## Container boundary

The Docker image uses a build stage with Maven/Java 21 and a smaller runtime
stage with the Java 21 JRE. The process runs as a non-root user.

Spring Boot Actuator exposes `/actuator/health`; Docker uses that endpoint as
its healthcheck.
