# aerosend-admin

Internal back office used by support and ops: customer accounts, inbox/domain overrides, IP pool management, whitelabel resellers, billing adjustments.

| | |
|---|---|
| Category | admin |
| Change frequency | **high** (support asks for new screens/actions constantly) |
| Stack | Java 21, Spring Boot 3, **Thymeleaf**, Spring Security (Google SSO), PostgreSQL read replica, RabbitMQ |
| Owners | Ops/Support tooling |

## Rules of the road

- **Reads** go straight to the Postgres read replica via `JdbcTemplate` (fast, flexible support queries).
- **Writes** never touch the DB directly. They go through `aerosend-core`'s API so the outbox/CDC events fire.
- Every mutating action is written to the `admin_audit_log` (see `config/AuditLogger`).
- Role checks: `ROLE_SUPPORT` (read + safe actions), `ROLE_OPS` (IP pools, rotation), `ROLE_ADMIN` (everything).

## Run locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local   # :8083, local profile disables SSO
```
