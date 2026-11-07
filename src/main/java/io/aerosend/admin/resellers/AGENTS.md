# resellers

## Overview
Read-only list of whitelabel resellers: agencies that sell Aerosend inboxes under their own brand (`ResellerController` Javadoc).

## Interface
- `ResellerController` (`@Controller`, `/resellers`):
  - `GET /resellers` -> `list(Model model)`, renders view `resellers/list` (`src/main/resources/templates/resellers/list.html`) with model attribute `resellers`.

## Invariants
- Access is enforced only by the `/resellers/**` URL rule (`ADMIN`) in [SecurityConfig](../config/AGENTS.md).
- `resellers` rows are maps from `queryForList`, so the SQL names `brand_name`, `custom_domain`, `sub_accounts`, and `monthly_inboxes` must match the template.

## Side Effects
- `list` reads table `resellers`, ordered by `brand_name`. This package performs no writes.

## Verification
- Run: `mvn test` (from `repos/admin`)
