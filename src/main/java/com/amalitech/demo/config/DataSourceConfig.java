package com.amalitech.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private final String url = System.getenv("DB_URL");
    private final String user = System.getenv("DB_SUSERNAME");
    private final String password = System.getenv("DB_SPASSWORD");

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(user);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}
