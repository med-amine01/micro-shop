package de.tekup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class WebSecurityProperties {

    @Value("${allowed.routes.post}")
    private String[] allowedPostRoutes;

    @Value("${allowed.routes.get}")
    private String[] allowedGetRoutes;

    public String[] getAllowedPostRoutes() {
        return allowedPostRoutes;
    }

    public String[] getAllowedGetRoutes() {
        return allowedGetRoutes;
    }
}
