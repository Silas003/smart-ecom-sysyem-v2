package com.amalitech.demo.metrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    // Placeholder bean to avoid depending on graphql-java APIs directly in this project.
    @Bean
    public GraphQLMetricsInstrumentation graphQLInstrumentation() {
        return new GraphQLMetricsInstrumentation();
    }
}
