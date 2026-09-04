package io.aerosend.admin.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Every mutating admin action must call this. Writes to the admin DB (not the core replica). */
@Component
public class AuditLogger {

    private final JdbcTemplate jdbc;

    public AuditLogger(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void record(String action, String targetType, Object targetId, String detail) {
        String actor = SecurityContextHolder.getContext().getAuthentication().getName();
        jdbc.update("INSERT INTO admin_audit_log (actor, action, target_type, target_id, detail) VALUES (?,?,?,?,?)",
            actor, action, targetType, String.valueOf(targetId), detail);
    }
}
