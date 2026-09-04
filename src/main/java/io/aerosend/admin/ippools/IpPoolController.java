package io.aerosend.admin.ippools;

import io.aerosend.admin.config.AuditLogger;
import io.aerosend.admin.customers.CoreAdminClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ip-pools")
public class IpPoolController {

    private final JdbcTemplate jdbc;
    private final CoreAdminClient core;
    private final AuditLogger audit;

    public IpPoolController(JdbcTemplate jdbc, CoreAdminClient core, AuditLogger audit) {
        this.jdbc = jdbc;
        this.core = core;
        this.audit = audit;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pools", jdbc.queryForList("""
            SELECT p.id, p.name, p.warmup_strategy,
                   count(ip.id) AS ips, round(avg(ip.health_score), 1) AS avg_health,
                   count(*) FILTER (WHERE ip.health_score < 70) AS unhealthy
            FROM ip_pools p LEFT JOIN ip_addresses ip ON ip.pool_id = p.id AND ip.active
            GROUP BY p.id ORDER BY p.name
            """));
        return "ippools/list";
    }

    @PostMapping("/ips/{ipId}/rotate")
    public String rotate(@PathVariable long ipId) {
        core.rotateIp(ipId);
        audit.record("ROTATE_IP", "ip", ipId, null);
        return "redirect:/ip-pools";
    }
}
