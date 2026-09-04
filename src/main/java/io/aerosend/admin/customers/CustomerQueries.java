package io.aerosend.admin.customers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerQueries {

    public record CustomerRow(long id, String company, String plan, int inboxes, int domains, int burnedDomains) {}

    private final JdbcTemplate jdbc;

    public CustomerQueries(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<CustomerRow> search(String q) {
        return jdbc.query("""
            SELECT c.id, c.company, c.plan,
                   (SELECT count(*) FROM mailboxes m JOIN domains d ON d.id = m.domain_id WHERE d.customer_id = c.id),
                   (SELECT count(*) FROM domains d WHERE d.customer_id = c.id),
                   (SELECT count(*) FROM domains d WHERE d.customer_id = c.id AND d.burn_status = 'BURNED')
            FROM customers c
            WHERE c.company ILIKE ? OR c.email ILIKE ?
            ORDER BY c.company
            LIMIT 100
            """,
            (rs, i) -> new CustomerRow(rs.getLong(1), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getInt(5), rs.getInt(6)),
            "%" + q + "%", "%" + q + "%");
    }
}
