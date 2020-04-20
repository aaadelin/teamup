package com.team.teamup.utils.query.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SearchField {
    String name() default "";
    String[] attributes() default "";
}
