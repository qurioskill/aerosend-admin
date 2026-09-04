package io.aerosend.admin.customers;

import io.aerosend.admin.config.AuditLogger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerQueries queries;
    private final CoreAdminClient core;
    private final AuditLogger audit;

    public CustomerController(CustomerQueries queries, CoreAdminClient core, AuditLogger audit) {
        this.queries = queries;
        this.core = core;
        this.audit = audit;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("customers", queries.search(q));
        return "customers/list";
    }

    @PostMapping("/{id}/domains/{domainId}/replace")
    @PreAuthorize("hasAnyRole('SUPPORT','ADMIN')")
    public String replaceBurnedDomain(@PathVariable long id, @PathVariable long domainId) {
        core.replaceDomain(domainId);
        audit.record("REPLACE_DOMAIN", "domain", domainId, "customer=" + id);
        return "redirect:/customers?q=";
    }
}
