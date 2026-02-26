package com.amalitech.demo.services.specification;

import com.amalitech.demo.models.Reviews;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification {

    public static Specification<Reviews> hasProductId(Long productId) {
        return (root, query, cb) ->
                productId == null ? cb.conjunction() :
                        cb.equal(root.get("product").get("id"), productId);
    }

    public static Specification<Reviews> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() :
                        cb.equal(root.get("user").get("id"), userId);
    }
}
