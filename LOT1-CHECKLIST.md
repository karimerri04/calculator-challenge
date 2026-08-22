# Lot 1 Checklist

## Design

- [ ] `TokenType` remains an enum.
- [ ] `Token` remains immutable.
- [ ] `Lexer` has one responsibility: lexical analysis.
- [ ] The lexer performs no arithmetic.
- [ ] Unary minus is not resolved by the lexer.
- [ ] Error messages include source positions.

## Supported syntax

- [ ] Integers
- [ ] Decimals
- [ ] `+`
- [ ] `-`
- [ ] `*`
- [ ] `/`
- [ ] `(`
- [ ] `)`
- [ ] Spaces
- [ ] Tabs/newlines if you choose `Character.isWhitespace`
- [ ] EOF

## Tests

- [ ] `"42"`
- [ ] `"3.14"`
- [ ] `"2+3"`
- [ ] `" 2 + 3 "`
- [ ] `"-5"`
- [ ] `"-1 - -1"`
- [ ] `"(2+5)*3"`
- [ ] `"2.8*3-1"`
- [ ] invalid character
- [ ] malformed decimal such as `"1.2.3"`

## Definition of done

```bash
mvn test
```

All tests pass and the lexer correctly tokenizes:

```text
-1 + 2.8 * (3 - 1)
```
