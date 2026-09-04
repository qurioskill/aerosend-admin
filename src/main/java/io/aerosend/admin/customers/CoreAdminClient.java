package io.aerosend.admin.customers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** All writes go through core so outbox events (and CDC) fire. */
@Component
public class CoreAdminClient {

    private final RestClient http;

    public CoreAdminClient(@Value("${aerosend.core-api.base-url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void replaceDomain(long domainId) {
        http.post().uri("/api/v1/domains/{id}/replace", domainId).retrieve().toBodilessEntity();
    }

    public void rotateIp(long ipId) {
        http.post().uri("/api/v1/ips/{id}/rotate", ipId).retrieve().toBodilessEntity();
    }
}
