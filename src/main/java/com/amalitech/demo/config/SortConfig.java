package com.amalitech.demo.config;

import com.amalitech.demo.utils.MergeSorter;
import com.amalitech.demo.utils.Sorter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class SortConfig {
    @Bean
    public Sorter mergeSorter() {
        // expose a raw Sorter bean backed by MergeSorter; services will inject with their specific generics
        return new MergeSorter();
    }
}
