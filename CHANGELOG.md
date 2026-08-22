# Changelog

## Repository organization

- organized the repository as a small monorepo with `backend/`, `frontend/`, and `docs/`;
- moved Maven and Docker backend assets under `backend/`;
- moved repository-wide formatting metadata to the root;
- removed generated build output, IDE metadata and delivery scratch files;
- updated CI and documentation for the new paths.

## Lot 8 — React frontend

- added React + TypeScript + Vite frontend under `frontend/`;
- added typed REST client with stable backend error propagation;
- added responsive expression workbench, examples and local history;
- added focused Vitest tests for frontend-owned behavior;
- added Vite `/api` development proxy;
- extended GitHub Actions with a frontend verification job;
- documented frontend/backend responsibility boundaries.

## Lot 7 — Test and documentation hardening

- expanded unit and application tests for lexical, syntax and evaluation cases;
- added length/combination/depth coverage for expressions;
- added a defensive exponent range guard (`-10000..10000`);
- documented testing strategy and REST contract;
- documented the planned React/TypeScript frontend boundary;
- documented the `BigDecimal` decision in ADR 0003;
- added `.editorconfig` and `.gitattributes` for repository consistency.

## Lot 6 — CI, quality and containerization

- GitHub Actions Java 21 verification;
- JaCoCo coverage gate;
- Docker multi-stage build and runtime health check;
- architecture and ADR documentation.

## Lot 5 — REST API

- Spring MVC calculation endpoint;
- Jakarta Validation;
- stable API error model and MockMvc tests.
