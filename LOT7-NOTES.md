# Lot 7 — Tests, documentation and Git readiness

This cumulative lot starts from Lot 6.

## Added

- stronger Lexer, Parser, Evaluator, Calculator and REST tests;
- expression tests covering length, operator combinations and nesting depth;
- exponent guard rail: supported integer exponent range is -10000 to 10000;
- `docs/testing-strategy.md`;
- `docs/api-contract.md`;
- `docs/frontend-plan.md`;
- ADR 0003 for BigDecimal;
- CHANGELOG, EditorConfig and Git attributes.

## Frontend

The frontend is intentionally not implemented in this lot. The next delivery
can add React + TypeScript + Vite under `frontend/` while keeping the backend at
repository root and using the REST API as the single source of calculation
truth.

## Verify

```bash
mvn clean verify
```
