package com.amalitech.demo.utils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.StringJoiner;

@Component("userKeyGenerator")
public class UserKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringJoiner sj = new StringJoiner(":");
        sj.add(method.getName());

        // params[0] = pageNumber, params[1] = pageSize
        sj.add("p" + params[0]);
        sj.add("s" + params[1]);

        return sj.toString();
    }
}
