# Lot 4 — Design Notes

## 1. `^` is part of the grammar

Exponentiation is not implemented as a string trick.
It is a real `BinaryOperator.POWER` node in the AST.

## 2. Right associativity

`2^3^2` means:

```text
2^(3^2)
```

and therefore returns `512`.

## 3. Power precedence over unary minus

```text
-2^2
```

is parsed as:

```text
-(2^2)
```

and returns `-4`.

```text
(-2)^2
```

returns `4`.

## 4. Generic function syntax

The lexer recognizes identifiers generically.

The parser maps known identifiers to `FunctionName`.

Today:

```text
SQRT
```

Tomorrow the same architecture can host more single-argument functions
without changing lexical analysis.

## 5. BigDecimal square root

Java's `BigDecimal.sqrt(MathContext)` is used.

## 6. Power scope

Integer exponents are supported, including negative integers.
Fractional exponents are deliberately rejected because supporting them
correctly would require a broader numeric-function policy.

This is preferable to silently converting to `double`.

## 7. No external math/parser library

The implementation remains transparent and explainable in an interview.
