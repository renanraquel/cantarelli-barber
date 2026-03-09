package com.gamecatarellibarber.config;

import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Converte a URL do Render (postgresql://) para o formato JDBC (jdbc:postgresql://)
 * quando necessário.
 */
public class DatabaseUrlProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       org.springframework.boot.SpringApplication application) {
        String url = environment.getProperty("JDBC_DATABASE_URL");
        if (url == null) {
            url = environment.getProperty("DATABASE_URL");
        }
        if (url != null && url.startsWith("postgresql://") && !url.startsWith("jdbc:")) {
            String jdbcUrl = "jdbc:" + url;
            Map<String, Object> props = new HashMap<>();
            props.put("JDBC_DATABASE_URL", jdbcUrl);
            environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlFix", props));
        }
    }
}
