package com.team.teamup.utils.query;

import java.util.function.Predicate;

public interface RecursivePredicate {

    Predicate<Object> execute(Object... arguments);
}
