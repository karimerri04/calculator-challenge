# Calculator Challenge

String-based calculator implemented as a small full-stack application.

The project parses mathematical expressions without an external parser/evaluator library and exposes the calculation engine through a REST API and a React interface.

## Repository structure

```text
calculator-challenge/
├── backend/              Java 21 / Spring Boot API and calculation engine
├── frontend/             React / TypeScript / Vite user interface
├── docs/                 Architecture, API contract, testing strategy and ADRs
└── .github/workflows/    Continuous integration
```

## Architecture

```text
React UI
   |
   | POST /api/v1/calculations
   v
Spring MVC adapter
   v
Calculator application facade
   v
Lexer -> Parser -> AST -> Evaluator -> BigDecimal
```

The calculation core is pure Java and has no Spring dependency.

## Supported expressions

```text
1 + 2            -> 3
1 + -1           -> 0
-1 - -1          -> 0
(2 + 5) * 3      -> 21
2 + 2 * 5 + 5    -> 17
2.8 * 3 - 1      -> 7.4
2^8              -> 256
2^3^2            -> 512
-2^2             -> -4
(-2)^2           -> 4
2^-2             -> 0.25
sqrt(4)          -> 2
1/0              -> error
```

## Backend

Requirements: Java 21 and Maven.

```bash
cd backend
mvn clean verify
mvn spring-boot:run
```

REST endpoint:

```text
POST http://localhost:8080/api/v1/calculations
```

Health endpoint:

```text
GET http://localhost:8080/actuator/health
```

If port `8080` is unavailable, Spring Boot can be started with `SERVER_PORT`, for example in PowerShell:

```powershell
$env:SERVER_PORT="8081"
mvn spring-boot:run
```

JaCoCo report:

```text
backend/target/site/jacoco/index.html
```

## Frontend

Requirements: Node.js 22+.

Start the backend first, then:

```bash
cd frontend
npm ci
npm test
npm run build
npm run dev
```

Open `http://localhost:5173`.

During development, Vite proxies `/api` to the backend on port `8080` by default. To use another backend address, copy `frontend/.env.example` to `frontend/.env` and set `VITE_DEV_PROXY_TARGET`. The frontend does not reimplement the expression grammar or calculation semantics.

## Docker

Build the backend image from the repository root:

```bash
docker build -t calculator-challenge-backend backend
```

Run it:

```bash
docker run --rm -p 8080:8080 calculator-challenge-backend
```

## Documentation

- [Architecture](docs/architecture.md)
- [API contract](docs/api-contract.md)
- [Testing strategy](docs/testing-strategy.md)
- [Frontend architecture](docs/frontend-plan.md)
- [Presentation guide](docs/presentation-guide.md)
- [Architecture Decision Records](docs/adr/)

## Main design decisions

- Java 21 and `BigDecimal` for deterministic decimal arithmetic.
- Hand-written lexer and recursive-descent parser.
- Immutable AST using sealed interfaces and records.
- Pure Java calculation core, isolated from Spring.
- REST adapter responsible for transport validation and HTTP error mapping.
- React frontend as a thin API client, not a second calculation engine.
- `mvn clean verify` with JaCoCo as the backend quality gate.
- GitHub Actions validates backend and frontend independently.
