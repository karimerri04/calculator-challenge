# ADR 0001 — Keep the calculator core independent from Spring

- Status: Accepted
- Date: 2026-08-22

## Context

The recruiting challenge is primarily about parsing and evaluating a string
expression. Spring is useful for exposing an HTTP API, but it is not part of
the mathematical problem itself.

## Decision

Keep lexer, parser, AST, operators, functions, and evaluator as pure Java.
Instantiate them from `CalculatorConfiguration` at the framework boundary.

## Consequences

### Positive

- core tests do not require a Spring context;
- business rules remain reusable from another adapter;
- framework concerns cannot leak into the parser/evaluator model;
- dependency direction is simple to explain in an interview.

### Trade-off

There is a small amount of explicit bean configuration instead of annotating
core classes with Spring stereotypes.
