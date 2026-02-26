package com.amalitech.demo.services.specification;

import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.models.Orders;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Orders> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() :
                        cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Orders> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<Orders> isBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();
            if (start != null && end != null) return cb.between(root.get("createdAt"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            return cb.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }
}
