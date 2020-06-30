package com.team.teamup.utils.query.comparator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractComparator {

    protected final Deque<Class<?>> classes;
    protected final Queue<Method> methods;

    public AbstractComparator(Deque<Class<?>> classes, Queue<Method> methods){
        this.classes = classes;
        this.methods = methods;
    }

    /**
     *
     * @param field class field
     * @return a new list containing all methods so far
     * @throws NoSuchMethodException if field doesn't have a getter
     */
    protected List<Method> getMethods(Field field) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        assert classes.peek() != null;
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return new ArrayList<>(this.methods);
    }

    /**
     * invoke all methods from a list to convet from an object to another. Used in imbricated objects
     * @param t initial object to convert
     * @param methods list of methods to apply in order
     * @return object after applying all the methods
     * @throws InvocationTargetException .
     * @throws IllegalAccessException .
     */
    public static Object invokeAllMethods(Object t, List<Method> methods) throws IllegalAccessException, InvocationTargetException {
        Object o = t;
        for (Method method : methods) {
            o = method.invoke(o);
        }
        return o;
    }
}
