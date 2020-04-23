package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.comparator.AbstractComparator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class EnumCompare extends AbstractComparator {

    public EnumCompare(Stack<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    public Predicate<Object> compare(Field field, String remainingCondition) {
        List<String> dateOperators = List.of("=", "!=", "in ");
        for (String operator : dateOperators) {
            if (remainingCondition.startsWith(operator)) {
                String rightOperand = remainingCondition.replaceFirst(operator, "").strip();
                try {
                    return enumOperator(field, operator, rightOperand);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        return t -> true;
    }

    private Predicate<Object> enumOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
        List<Method> methodsCopy = getMethods(field);
        return t -> {
            try {
                Object leftOperand = invokeAllMethods(t, methodsCopy);
                if (leftOperand == null) {
                    return true;
                }
                switch (operator) {
                    case "=":
                        return rightOperand.equalsIgnoreCase(leftOperand.toString());
                    case "!=":
                        return !rightOperand.equalsIgnoreCase(leftOperand.toString());
                    case "in ":
                        String[] options = rightOperand.split("\\[")[1].split("]")[0].split(",");
                        return Arrays.stream(options).map(String::strip).anyMatch(s -> s.equalsIgnoreCase(leftOperand.toString()));
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.info(e.getMessage());
            }
            return true;
        };

    }

}
