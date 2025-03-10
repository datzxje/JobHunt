package com.jobhunt.specification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SearchCriteria {
    private final String key;
    private final String operation;
    private final Object value;
    private final String predicateType;

    public boolean isOrPredicate() {
        return "OR".equalsIgnoreCase(predicateType);
    }
}
