# Calculator Challenge Frontend

React + TypeScript + Vite adapter for the Java calculator API.

## Architectural rule

The browser does **not** implement calculator semantics. It sends the raw
expression to `POST /api/v1/calculations` and displays the backend result or
backend error contract.

## Requirements

- Node.js 22 or newer
- backend running locally

## Install

```bash
npm ci
```

The committed `package-lock.json` keeps CI and local installs reproducible.

## Run

```bash
npm run dev
```

Open `http://localhost:5173`.

By default, Vite proxies `/api` to `http://localhost:8080`. If the backend uses
another port, copy `.env.example` to `.env` and change:

```text
VITE_DEV_PROXY_TARGET=http://localhost:8081
```

No permissive CORS rule is needed for local development.

## Verify

```bash
npm test
npm run build
```

The unit tests focus on the API boundary and local history behavior. Backend
parser/evaluator semantics remain covered by the Java test suite rather than
being duplicated here.

## Optional deployed API base URL

For a deployment where the frontend and backend do not share an origin:

```text
VITE_API_BASE_URL=https://calculator.example.com
```

A production deployment should still prefer a same-origin reverse proxy when
possible.
