# Lot 1 Notes

## Architectural decisions

### 1. Lexer is pure Java
No Spring annotations are used in the core.

### 2. `Token` is a record
Tokens are immutable value objects.

### 3. Unary minus is syntax, not lexical semantics
`-5` => `MINUS`, `NUMBER("5")`.

### 4. Source positions are preserved
This enables useful diagnostics later from both lexer and parser.

### 5. Output list is immutable
`List.copyOf(tokens)` prevents accidental mutation outside the lexer.

## Next lot

The next step is a parser producing an AST:

```text
List<Token> -> Parser -> Expression AST
```

Recommended AST:

```text
sealed interface Expression
  NumberExpression
  UnaryExpression
  BinaryExpression
```
