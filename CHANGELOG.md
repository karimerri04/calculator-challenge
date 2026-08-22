# Changelog

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
