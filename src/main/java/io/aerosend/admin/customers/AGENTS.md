# customers

## Overview
Customer search for support staff, the burned-domain replacement action, and `CoreAdminClient`, the HTTP client for write operations on `aerosend-core`.

## Interface
- `CustomerController` (`@Controller`, `/customers`):
  - `GET /customers?q=` -> `list(String q, Model model)`, renders view `customers/list` (`src/main/resources/templates/customers/list.html`) with model attributes `q` and `customers`.
  - `POST /customers/{id}/domains/{domainId}/replace` -> `replaceBurnedDomain(long id, long domainId)`, redirects to `/customers?q=`.
- `CustomerQueries` (`@Repository`): `search(String q)` returns `List<CustomerRow>`.
- `CustomerQueries.CustomerRow` (record): `id`, `company`, `plan`, `inboxes`, `domains`, `burnedDomains`.
- `CoreAdminClient` (`@Component`): `replaceDomain(long domainId)`, `rotateIp(long ipId)`. Configured by property `aerosend.core-api.base-url`. Also used by [ippools](../ippools/AGENTS.md).

## Invariants
- All writes go through core so outbox events and CDC fire (`CoreAdminClient` Javadoc). Do not add direct SQL writes for core data here.
- `replaceBurnedDomain` carries `@PreAuthorize("hasAnyRole('SUPPORT','ADMIN')")`, which is narrower than the URL rule: `OPS` can reach `/customers` but cannot run this action.
- `replaceBurnedDomain` calls core first and `AuditLogger.record` second, with action `REPLACE_DOMAIN`, target type `domain`, and detail `customer=<id>`. The two steps are not transactional.
- `search` matches `c.company ILIKE ?` or `c.email ILIKE ?` with `%q%`, orders by `c.company`, and has `LIMIT 100`. An empty `q` matches every customer up to the limit.
- `CustomerRow` is mapped by column position, so the `SELECT` order and the `rs.get*` indexes must change together.
- A burned domain is one with `d.burn_status = 'BURNED'`.

## Side Effects
- `search` reads tables `customers`, `domains`, and `mailboxes`.
- `replaceDomain` sends `POST /api/v1/domains/{id}/replace` to core; `rotateIp` sends `POST /api/v1/ips/{id}/rotate`. `CoreAdminClient` sets no auth header or explicit timeout.
- `replaceBurnedDomain` inserts into `admin_audit_log` through [AuditLogger](../config/AGENTS.md).

## Verification
- Run: `mvn test` (from `repos/admin`)
