package com.amalitech.demo.utils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.StringJoiner;

@Component("orderSearchKeyGenerator")
public class OrderSearchKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // Use method name as prefix to keep "Orders" separate from "Products"
        StringJoiner sj = new StringJoiner(":");
        sj.add(method.getName());

        for (Object param : params) {
            if (param == null) {
                sj.add("any");
            } else if (param instanceof Pageable p) {
                sj.add(formatPageable(p));
            } else if (param instanceof LocalDateTime ldt) {
                // Convert to Epoch seconds to avoid format mismatches
                sj.add(String.valueOf(ldt.toEpochSecond(ZoneOffset.UTC)));
            } else if (param instanceof Enum<?> e) {
                sj.add(e.name());
            } else if (param instanceof Collection<?> col) {
                // For "search by list of IDs" methods
                sj.add("list" + col.hashCode());
            } else {
                sj.add(param.toString());
            }
        }
        return sj.toString();
    }

    private String formatPageable(Pageable p) {
        return String.format("p%d:s%d:%s",
                p.getPageNumber(),
                p.getPageSize(),
                p.getSort().isSorted() ? p.getSort().toString().replace(": ", "-") : "none");
    }
}