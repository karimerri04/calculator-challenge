# Frontend Integration Plan

A frontend is planned as a separate delivery lot. It must remain an adapter of
the calculator API rather than a second calculator implementation.

## Proposed stack

- React
- TypeScript
- Vite
- native `fetch` or a very small API client wrapper
- component tests only where they add value

## Responsibility split

```text
Browser UI
   |
   | POST /api/v1/calculations
   v
Spring REST adapter
   v
Calculator application/core
```

The frontend is responsible for:

- expression input;
- submit/loading states;
- displaying the exact result string;
- displaying API errors clearly;
- keyboard accessibility and responsive layout.

The backend remains the source of truth for:

- tokenization;
- grammar;
- operator precedence;
- arithmetic semantics;
- validation of supported expressions.

## Local development

Prefer a Vite development proxy for `/api` to `http://localhost:8080`. This
avoids adding broad CORS rules to the backend solely for local development.
Production can serve both applications behind the same origin or configure a
narrow explicit CORS policy if deployment requires separate origins.

## Repository layout

The Maven backend stays at repository root for the Java challenge. The future
frontend can be added under:

```text
frontend/
```

This avoids moving the already-reviewed backend while still allowing a simple
monorepo and separate CI jobs.
