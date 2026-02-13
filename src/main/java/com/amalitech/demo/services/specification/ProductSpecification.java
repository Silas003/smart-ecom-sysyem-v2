package com.amalitech.demo.services.specification;

import com.amalitech.demo.models.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> hasName(String name){
        return(root,criteriaQuery,criteriaBuilder)->
                name == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? cb.conjunction() :
                        cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            if (minPrice != null && maxPrice != null) return cb.between(root.get("price"), minPrice, maxPrice);
            if (minPrice != null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
}

//class CritrialApi {
//    static EntityManager entityManager;
//    public static void main(String[] args) {
//        CriteriaBuilder criteriaBuilder= entityManager.getCriteriaBuilder();
//        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
//        Root<Product> root = criteriaQuery.from(Product.class);
//        Predicate predicate = criteriaBuilder.equal(root.get("id"), 1L);
//        Predicate predicate2 = criteriaBuilder.equal(root.get("name"), "mango");
//        criteriaQuery.where(criteriaBuilder.and(predicate,predicate2));
//
//        var results = entityManager.createQuery(criteriaQuery).getResultList();
//
//    }
//}