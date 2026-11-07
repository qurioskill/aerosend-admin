# config

## Overview
Cross-cutting infrastructure for the admin app: the HTTP security filter chain and the audit log writer that mutating controllers depend on.

## Interface
- `AuditLogger` (`@Component`): `record(String action, String targetType, Object targetId, String detail)`.
- `SecurityConfig` (`@Configuration`, `@EnableMethodSecurity`): package-private `filterChain(HttpSecurity)` bean of type `SecurityFilterChain`.

URL rules declared in `SecurityConfig.filterChain`, in match order:

| Matcher | Access |
|---|---|
| `/actuator/health` | `permitAll` |
| `/ip-pools/**` | `OPS`, `ADMIN` |
| `/resellers/**` | `ADMIN` |
| any other request | `SUPPORT`, `OPS`, `ADMIN` |

## Invariants
- Every mutating admin action must call `AuditLogger.record` (`AuditLogger` Javadoc).
- `AuditLogger.record` takes the actor from `SecurityContextHolder.getContext().getAuthentication().getName()`, so it must run with an authenticated security context.
- `targetId` is stored as `String.valueOf(targetId)`; `detail` may be `null` (`IpPoolController.rotate` passes `null`).
- Matcher order in `filterChain` is significant: the catch-all `anyRequest()` must stay last.
- `@EnableMethodSecurity` must stay on `SecurityConfig`, because `CustomerController.replaceBurnedDomain` relies on `@PreAuthorize`.

## Side Effects
- `AuditLogger.record` runs `INSERT INTO admin_audit_log (actor, action, target_type, target_id, detail)` through the injected `JdbcTemplate`. Its Javadoc says this targets the admin DB rather than the core replica, but `src/main` defines no second `DataSource`, so it uses the single datasource in `src/main/resources/application.yml`. That datasource's username is `aerosend_ro`, and its comment names a read replica in production, so these inserts may fail there.
- `SecurityConfig` enables OAuth2 login with login page `/oauth2/authorization/google`. It does not disable CSRF protection.

## Verification
- Run: `mvn test` (from `repos/admin`)
