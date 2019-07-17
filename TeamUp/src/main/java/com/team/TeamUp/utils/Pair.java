package com.team.TeamUp.utils;

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
