package com.bizflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication(
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class BackendApplication {

    public static void main(String[] args) {
        normalizeRenderDatabaseUrl();
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void normalizeRenderDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            return;
        }

        String uriValue = databaseUrl;
        if (databaseUrl.startsWith("jdbc:")) {
            uriValue = databaseUrl.substring("jdbc:".length());
        }

        if (uriValue.startsWith("postgresql://") || uriValue.startsWith("postgres://")) {
            URI renderDatabaseUrl = URI.create(uriValue);
            if (renderDatabaseUrl.getHost() == null || renderDatabaseUrl.getPath() == null) {
                throw new IllegalStateException("DATABASE_URL must contain a valid PostgreSQL host and database");
            }

            String userInfo = renderDatabaseUrl.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                String[] credentials = userInfo.split(":", 2);
                String username = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8);
                String password = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8);
                System.setProperty("spring.datasource.username", username);
                System.setProperty("spring.datasource.password", password);
            }

            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                    .append(renderDatabaseUrl.getHost());
            if (renderDatabaseUrl.getPort() > 0) {
                jdbcUrl.append(':').append(renderDatabaseUrl.getPort());
            }
            jdbcUrl.append(renderDatabaseUrl.getPath());
            if (renderDatabaseUrl.getQuery() != null) {
                jdbcUrl.append('?').append(renderDatabaseUrl.getQuery());
            }

            System.setProperty("spring.datasource.url", jdbcUrl.toString());
        }
    }
}
