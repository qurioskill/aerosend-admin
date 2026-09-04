package io.aerosend.admin.resellers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Whitelabel resellers: agencies that sell Aerosend inboxes under their own brand. */
@Controller
@RequestMapping("/resellers")
public class ResellerController {

    private final JdbcTemplate jdbc;

    public ResellerController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("resellers", jdbc.queryForList(
            "SELECT id, brand_name, custom_domain, sub_accounts, monthly_inboxes FROM resellers ORDER BY brand_name"));
        return "resellers/list";
    }
}
