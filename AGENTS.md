# aerosend-admin Blueprint

## System Overview
Internal back office built on Java 21, Spring Boot 3.3.5, and Thymeleaf. It renders server-side pages for customers, IP pools, and resellers. Reads use `JdbcTemplate` SQL against the configured datasource, and actions on core data are sent to the `aerosend-core` HTTP API.

## Subdirectories & System Boundaries
- src: [./src/AGENTS.md](./src/AGENTS.md) - Maven source tree; the application code is in [io.aerosend.admin](./src/main/java/io/aerosend/admin/AGENTS.md).

## Global Invariants
- All writes go through core so outbox events and CDC fire (`CoreAdminClient` Javadoc). `AuditLogger` is the only direct SQL write in `src/main`.
- Every mutating admin action must call `AuditLogger.record` (`AuditLogger` Javadoc).
- Role rules live in `SecurityConfig.filterChain`, plus `@PreAuthorize` on `CustomerController.replaceBurnedDomain`.
- SQL column names, record fields, and Thymeleaf template expressions are coupled by name or position, with no shared type for the map-based pages.

## Global Side Effects
- PostgreSQL via `spring.datasource`: reads `customers`, `domains`, `mailboxes`, `ip_pools`, `ip_addresses`, and `resellers`; inserts into `admin_audit_log`.
- `aerosend-core` HTTP API at `aerosend.core-api.base-url`: `POST /api/v1/domains/{id}/replace` and `POST /api/v1/ips/{id}/rotate`.
- OAuth2 login through `/oauth2/authorization/google`.

## System Verification
- Run: `mvn test` (from `repos/admin`)
- The only test, `CustomerControllerTest`, is `@Disabled`, so the command currently verifies compilation only.
