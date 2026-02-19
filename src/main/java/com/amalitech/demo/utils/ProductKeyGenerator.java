package com.amalitech.demo.utils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.StringJoiner;

@Component("productKeyGenerator")
public class ProductKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // Use a StringJoiner for clean delimiters (e.g., "methodName:arg1:arg2")
        StringJoiner key = new StringJoiner(":");

        // 1. Add class and method name to prevent collisions with other cached methods
        key.add(target.getClass().getSimpleName());
        key.add(method.getName());

        for (Object param : params) {
            if (param == null) {
                key.add("any");
            } else if (param instanceof Pageable pageable) {
                // 2. Extract specific pagination details
                key.add("p" + pageable.getPageNumber());
                key.add("s" + pageable.getPageSize());

                // 3. Handle Sort (ensures 'price,asc' != 'price,desc')
                if (pageable.getSort().isSorted()) {
                    key.add(pageable.getSort().toString().replace(": ", "-"));
                } else {
                    key.add("unsorted");
                }
            } else if (param instanceof String str) {
                // 4. Sanitize strings (remove spaces/lowercase) for consistent keys
                key.add(StringUtils.hasText(str) ? str.trim().toLowerCase() : "any");
            } else {
                // 5. Fallback for Long, Double, etc.
                key.add(param.toString());
            }
        }

        return key.toString();
    }
}
