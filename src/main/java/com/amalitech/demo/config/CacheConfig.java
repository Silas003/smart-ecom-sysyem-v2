package com.amalitech.demo.config;

import com.amalitech.demo.dto.TokenValidationResult;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${security.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "orderByUser",
                "order",
                "user",
                "productsByCategory",
                "product",
                "category",
                "allcategories",
                "activeUserCart",
                "userCount",
                "averageRating",
                "tokenBlacklist",
                "products",
                "reviewsByProduct",
                "orders",
                "ordersByUser",
                "inventory",
                "review",
                "reviewsByUser",
                "reviews",
                "reviewsByProduct",
                "users",
                "userAuth"
        );

        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .recordStats()
        );


        Cache<Object, Object> tokenCache = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfterWrite(jwtExpirationMs, TimeUnit.MILLISECONDS)
                .recordStats()
                .build();

        caffeineCacheManager.registerCustomCache("tokenBlacklist", tokenCache);

        return caffeineCacheManager;
    }

    @Bean
    public Cache<String, TokenValidationResult> tokenCache() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }
}
