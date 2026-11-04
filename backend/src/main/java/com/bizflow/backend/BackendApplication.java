package com.bizflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

import java.net.URI;

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
