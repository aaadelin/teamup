package com.team.teamup.utils.query.comparator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public abstract class AbstractComparator {

    protected final Stack<Class<?>> classes;
    protected final Queue<Method> methods;

    public AbstractComparator(Stack<Class<?>> classes, Queue<Method> methods){
        this.classes = classes;
        this.methods = methods;
    }

    protected List<Method> getMethods(Field field) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return new ArrayList<>(this.methods);
    }


    public static Object invokeAllMethods(Object t, List<Method> methods) throws InvocationTargetException, IllegalAccessException {
        Object o = t;
        for (Method method : methods) {
            o = method.invoke(o);
        }
        return o;
    }
}
