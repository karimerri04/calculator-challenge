# Frontend Architecture and Integration

## Status

Implemented in Lot 8 under `frontend/`.

## Purpose

The frontend is deliberately a thin inbound adapter over the Java calculator.
It improves usability and demonstrates end-to-end integration without creating
a second expression engine in TypeScript.

## Stack

- React 19
- TypeScript
- Vite
- native Fetch API
- Vitest for focused unit tests

No state-management or HTTP-client library is introduced because the page does
not need one.

## Responsibility split

```text
Browser UI
   |
   | POST /api/v1/calculations
   v
Spring MVC adapter
   v
Calculator application facade
   v
Lexer -> Parser -> AST -> Evaluator -> BigDecimal
```

### Frontend owns

- expression input and submission state;
- rendering exact result strings returned by the API;
- clear presentation of API error codes/messages;
- reusable example expressions;
- a bounded browser-local history of successful calculations;
- keyboard-accessible and responsive interaction.

### Backend remains authoritative for

- lexical rules;
- grammar and precedence;
- unary/binary operator semantics;
- power and square-root semantics;
- validation of supported expressions;
- numeric precision and calculation errors.

This boundary avoids semantic drift between Java and JavaScript implementations.

## API client

`frontend/src/api/calculations.ts` is the only code that knows the calculation
endpoint. It maps network failures to `NETWORK_ERROR` while preserving the
backend's stable error codes unchanged.

## Local development proxy

`vite.config.ts` proxies `/api` to `http://localhost:8080`.

```text
Browser :5173 -> /api -> Vite proxy -> Spring Boot :8080
```

This is preferable to adding `@CrossOrigin("*")` solely for development.

## Local history

Only successful calculations are stored. The list:

- uses `localStorage`;
- is capped at eight entries;
- contains no server-side persistence;
- can be cleared by the user;
- is tested independently from React rendering.

## Tests

Frontend unit tests intentionally target its own responsibilities:

- successful REST response mapping;
- preservation of backend error contracts;
- network-failure behavior;
- bounded history;
- corrupt local-storage recovery.

Parser/evaluator test cases stay in Java. Repeating them in TypeScript would test
a duplicate implementation that the architecture explicitly avoids.

## Production note

The cleanest deployment is same-origin: reverse proxy `/api` to Spring Boot and
serve the compiled frontend from the same public host. If separate origins are
required, configure a narrow backend CORS policy and set `VITE_API_BASE_URL` at
frontend build time.
