package com.realstate.habitar.infraestructure.config.hubspot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hubspot")
@Data
public class HubspotProperties {

    private String baseUrl;
    private final DealApp dealApp = new DealApp();
    private final Api api = new Api();

    @Data
    public static class DealApp{
        private String token;
    }

    @Data
    public static class Api{
        private String deals;
        private String owners;
    }
}
