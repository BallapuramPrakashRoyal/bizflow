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
        if (databaseUrl != null
                && (databaseUrl.startsWith("postgresql://")
                || databaseUrl.startsWith("postgres://"))) {
            URI renderDatabaseUrl = URI.create(databaseUrl);
            if (renderDatabaseUrl.getHost() == null || renderDatabaseUrl.getPath() == null) {
                throw new IllegalStateException("DATABASE_URL must contain a valid PostgreSQL host and database");
            }

            String userInfo = renderDatabaseUrl.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                String[] credentials = userInfo.split(":", 2);
                System.setProperty(
                        "DATABASE_USERNAME",
                        URLDecoder.decode(credentials[0], StandardCharsets.UTF_8)
                );
                System.setProperty(
                        "DATABASE_PASSWORD",
                        URLDecoder.decode(credentials[1], StandardCharsets.UTF_8)
                );
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

            System.setProperty("DATABASE_URL", jdbcUrl.toString());
        }
    }
}
