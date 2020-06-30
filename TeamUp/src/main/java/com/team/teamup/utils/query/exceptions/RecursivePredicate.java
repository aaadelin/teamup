package com.team.teamup.utils.query.exceptions;

import java.util.function.Predicate;

public interface RecursivePredicate {

    Predicate<Object> execute(Object... arguments);
}
