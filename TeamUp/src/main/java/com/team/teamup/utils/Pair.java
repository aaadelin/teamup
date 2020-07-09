package com.team.teamup.utils;

import lombok.ToString;

/**
 * Pair utility class
 * @param <S> type of the first member of the pair
 * @param <T> type of the second member of the pair
 */
@ToString
public class Pair<S, T> {

    private S entity1;
    private T entity2;

    public Pair() {
    }

    public Pair(S entity1, T entity2) {
        this.entity1 = entity1;
        this.entity2 = entity2;
    }

    public S getKey() {
        return entity1;
    }

    public void setKey(S entity1) {
        this.entity1 = entity1;
    }

    public T getValue() {
        return entity2;
    }

    public void setValue(T entity2) {
        this.entity2 = entity2;
    }
}
