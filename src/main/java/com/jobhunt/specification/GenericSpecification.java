package com.jobhunt.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GenericSpecification<T> {
    private final List<SearchCriteria> params;

    public GenericSpecification() {
        this.params = new ArrayList<>();
    }

    // Add criteria to the specification
    public GenericSpecification<T> with(String key, String operation, Object value, String predicateType) {
        if (key != null && operation != null && value != null) {
            params.add(new SearchCriteria(key, operation, value, predicateType));
        }
        return this;
    }

    // Build the specification
    public Specification<T> build() {
        if (params.isEmpty()) {
            return (root, query, builder) -> builder.conjunction(); // Return a no-op specification
        }

        List<Specification<T>> specs = params.stream()
                .map(this::getSpecification)
                .toList();

        Specification<T> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            SearchCriteria criteria = params.get(i - 1);
            if (criteria.isOrPredicate()) {
                result = Specification.where(result).or(specs.get(i));
            } else {
                result = Specification.where(result).and(specs.get(i));
            }
        }

        return result;
    }

    private Specification<T> getSpecification(SearchCriteria criteria) {
        return (root, query, builder) -> genericCriteria(criteria, root, builder);
    }

    private Predicate genericCriteria(SearchCriteria criteria, Root<?> root, CriteriaBuilder builder) {
        String operation = criteria.getOperation();
        String key = criteria.getKey();
        Object value = criteria.getValue();

        switch (operation) {
            case ">":
                return builder.greaterThanOrEqualTo(root.get(key), value.toString());
            case "<":
                return builder.lessThanOrEqualTo(root.get(key), value.toString());
            case "=":
                if (root.get(key).getJavaType() == String.class) {
                    return builder.like(root.get(key), "%" + value + "%");
                } else {
                    return builder.equal(root.get(key), value);
                }
            default:
                throw new UnsupportedOperationException("Operation " + operation + " is not supported");
        }
    }
}

