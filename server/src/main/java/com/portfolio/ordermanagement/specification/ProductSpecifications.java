package com.portfolio.ordermanagement.specification;

import com.portfolio.ordermanagement.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecifications {

    private ProductSpecifications(){}

    public static Specification<Product> nameContains(String search) {
        return (root,query, criteriaBuilder) -> {
            if(search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId){
        return (root,query, criteriaBuilder) -> {
            if(categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                   root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }
}