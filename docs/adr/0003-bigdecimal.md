# ADR 0003 — Use BigDecimal for calculation values

## Status

Accepted.

## Context

The challenge requires decimal arithmetic. Binary floating-point types such as
`double` can produce representation artifacts for common decimal values.

## Decision

Represent parsed numbers and calculated values with `BigDecimal` and evaluate
with a shared `MathContext(34, HALF_EVEN)`.

The REST API returns the result as a string so a browser client can display the
backend result without an implicit JavaScript floating-point conversion.

## Consequences

Positive:

- deterministic decimal semantics;
- explicit precision and rounding policy;
- exact representation for decimal literals used by the challenge.

Trade-offs:

- non-terminating division requires a `MathContext`;
- exponentiation is deliberately restricted to integer exponents;
- a guard rail limits absolute exponent size to avoid pathological work.
