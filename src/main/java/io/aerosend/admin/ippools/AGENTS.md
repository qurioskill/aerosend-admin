# ippools

## Overview
IP pool health overview and the IP rotation action for ops staff.

## Interface
- `IpPoolController` (`@Controller`, `/ip-pools`):
  - `GET /ip-pools` -> `list(Model model)`, renders view `ippools/list` (`src/main/resources/templates/ippools/list.html`) with model attribute `pools`.
  - `POST /ip-pools/ips/{ipId}/rotate` -> `rotate(long ipId)`, redirects to `/ip-pools`.

## Invariants
- Access is enforced only by the `/ip-pools/**` URL rule (`OPS`, `ADMIN`) in [SecurityConfig](../config/AGENTS.md); this controller has no `@PreAuthorize`.
- `rotate` writes through `CoreAdminClient.rotateIp` from [customers](../customers/AGENTS.md), then calls `AuditLogger.record` with action `ROTATE_IP`, target type `ip`, and `null` detail. The two steps are not transactional.
- `list` counts only active IPs (`ip.active` in the join condition) and treats `ip.health_score < 70` as unhealthy.
- `pools` rows are maps from `queryForList`, so the SQL names `name`, `warmup_strategy`, `ips`, `avg_health`, and `unhealthy` must match the template.

## Side Effects
- `list` reads tables `ip_pools` and `ip_addresses`.
- `rotate` sends `POST /api/v1/ips/{id}/rotate` to core and inserts into `admin_audit_log`.

## Verification
- Run: `mvn test` (from `repos/admin`)
