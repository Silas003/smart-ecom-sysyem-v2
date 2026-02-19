package com.amalitech.demo.utils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.StringJoiner;

@Component("reviewKeyGenerator")
public class ReviewKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringJoiner sj = new StringJoiner(":");
        sj.add(method.getName());

        // Params[0] = productId, Params[1] = userId
        Long productId = (Long) params[0];
        Long userId = (Long) params[1];

        sj.add("p" + (productId != null ? productId : "any"));
        sj.add("u" + (userId != null ? userId : "any"));

        return sj.toString();
    }
}