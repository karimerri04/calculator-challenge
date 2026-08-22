# REST API Contract

## Calculate an expression

`POST /api/v1/calculations`

### Request

```json
{
  "expression": "sqrt(4) + 2^3"
}
```

Rules:

- `expression` is mandatory;
- blank expressions are rejected;
- maximum length is 1000 characters.

### Success response

HTTP `200 OK`

```json
{
  "expression": "sqrt(4) + 2^3",
  "result": "10"
}
```

`result` is intentionally serialized as a string. The backend uses
`BigDecimal`, and the HTTP contract should not force a JavaScript client to
convert the value through IEEE-754 floating-point representation.

### Error response

HTTP `400 Bad Request`

```json
{
  "code": "CALCULATION_ERROR",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/calculations"
}
```

Stable error codes:

- `VALIDATION_ERROR`
- `LEXICAL_ERROR`
- `SYNTAX_ERROR`
- `CALCULATION_ERROR`

The message is intended for a human-readable frontend error state. Client code
should branch on `code`, not parse message text.
