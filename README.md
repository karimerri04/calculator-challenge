# Calculator Challenge — Lot 5

> Reference implementation for study and local practice.
> Do not submit it unchanged if the recruiting challenge prohibits code produced by others.

## Architecture

```text
HTTP
  |
  v
REST adapter — Spring MVC
  |
  v
Calculator application facade
  |
  +-> Lexer
  +-> Parser
  `-> Evaluator
```

The calculator core remains pure Java.

## Stack

- Java 21 LTS
- Spring Boot 4.1.1
- Spring MVC
- Jakarta Validation
- Maven
- JUnit / AssertJ / MockMvc
- BigDecimal

## Endpoint

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

Response:

```json
{
  "expression": "2 + 2 * 5 + 5",
  "result": "17"
}
```

## Errors

Example:

```json
{
  "code": "CALCULATION_ERROR",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/calculations"
}
```

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Then call:

```text
POST http://localhost:8080/api/v1/calculations
```

with:

```json
{
  "expression": "sqrt(4) + 2^3"
}
```

Expected result:

```json
{
  "expression": "sqrt(4) + 2^3",
  "result": "10"
}
```
