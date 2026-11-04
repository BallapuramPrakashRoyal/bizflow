package com.bizflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

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
            System.setProperty(
                    "DATABASE_URL",
                    "jdbc:postgresql://" + databaseUrl.substring(databaseUrl.indexOf("://") + 3)
            );
        }
    }
}