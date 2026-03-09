package com.gamecatarellibarber.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Configura o DataSource para Render: converte postgresql:// em jdbc:postgresql://
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "JDBC_DATABASE_URL")
    public DataSource dataSource(
            @Value("${JDBC_DATABASE_URL}") String jdbcUrl,
            @Value("${JDBC_DATABASE_USERNAME:}") String username,
            @Value("${JDBC_DATABASE_PASSWORD:}") String password) {
        String url = jdbcUrl;
        if (url.startsWith("postgresql://") && !url.startsWith("jdbc:")) {
            url = "jdbc:" + url;
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        if (username != null && !username.isEmpty()) {
            ds.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            ds.setPassword(password);
        }
        return ds;
    }
}
