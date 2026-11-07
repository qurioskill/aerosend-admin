# io.aerosend.admin

## Overview
Root Java package of `aerosend-admin`: the Spring Boot entry point and the feature packages for the back office.

## Interface
- `AdminApplication` (`@SpringBootApplication`): `main(String[] args)`.
- [config](./config/AGENTS.md): `AuditLogger` and `SecurityConfig`.
- [customers](./customers/AGENTS.md): customer search, domain replacement, and `CoreAdminClient`.
- [ippools](./ippools/AGENTS.md): IP pool list and IP rotation.
- [resellers](./resellers/AGENTS.md): read-only reseller list.

## Invariants
- `AdminApplication` must stay in `io.aerosend.admin`: `@SpringBootApplication` component scanning starts at this package and covers the feature packages below it.

## Side Effects
- `main` starts the Spring Boot application. `src/main/resources/application.yml` sets `server.port` to `8083`.

## Verification
- Run: `mvn test` (from `repos/admin`)
