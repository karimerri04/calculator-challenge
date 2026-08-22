# ADR 0002 — Use a handwritten recursive-descent parser

- Status: Accepted
- Date: 2026-08-22

## Context

The grammar is intentionally small, but it must express precedence,
parentheses, unary minus, right-associative exponentiation, and functions.
External parser/evaluator libraries would hide the main reasoning expected by
the challenge.

## Decision

Use a handwritten recursive-descent parser with one method per grammar level.
Keep parse state inside a private `ParseSession`, making the public `Parser`
object stateless.

## Consequences

### Positive

- precedence is visible directly in the call structure;
- right associativity of `^` is explicit;
- syntax errors can include token positions;
- no parser dependency is required.

### Trade-off

Adding multi-argument functions such as `min(a,b)` requires a deliberate
grammar extension for commas and argument lists.
