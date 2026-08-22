# Calculator Challenge Frontend

React + TypeScript + Vite adapter for the Java calculator API.

## Architectural rule

The browser does **not** implement calculator semantics. It sends the raw
expression to `POST /api/v1/calculations` and displays the backend result or
backend error contract.

## Requirements

- Node.js 22 or newer
- backend running on `http://localhost:8080` for local development

## Install

```bash
npm install
```

`npm install` will generate `package-lock.json`. Commit that lock file before
final delivery so CI can be changed to `npm ci` in the final audit lot.

## Run

```bash
npm run dev
```

Open `http://localhost:5173`.

Vite proxies `/api` to `http://localhost:8080`; no permissive CORS rule is
needed for local development.

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
