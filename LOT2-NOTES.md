# Lot 2 — Design Notes

## Why a recursive-descent parser?

The grammar is small, deterministic and easy to map to Java methods.
This gives explicit control over precedence without a third-party parser.

## Why an AST?

The parser is responsible only for syntax. Evaluation comes later.

```text
2 + 3 * 4
```

becomes conceptually:

```text
      ADD
     /   \
    2   MULTIPLY
       /        \
      3          4
```

The evaluator will walk that tree in Lot 3.

## Why `sealed interface Expression`?

The expression hierarchy is closed and explicit.
This is useful later with exhaustive pattern matching.

## Why records?

AST nodes are immutable value objects. Records fit that model naturally.

## Why `BigDecimal` in `NumberExpression`?

Lexical representation remains a String in `Token`.
Once syntax recognizes a numeric literal, the AST represents its semantic numeric value.

## Why is Parser stateless?

`Parser.parse(...)` creates an internal ParseSession.
No parsing position is stored on the Parser itself, making the parser reusable and safe to share.

## Deliberately not implemented yet

- evaluation
- exponentiation
- functions such as `sqrt`
- REST endpoint
- GitHub Actions
- Docker

Those will be introduced only when their responsibilities become relevant.
