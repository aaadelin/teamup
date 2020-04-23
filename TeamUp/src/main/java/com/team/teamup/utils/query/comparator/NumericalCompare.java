package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.comparator.AbstractComparator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static com.team.teamup.utils.query.AbstractLanguageParser.isNumber;

@Slf4j
public class NumericalCompare extends AbstractComparator {

    public NumericalCompare(Stack<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    public Predicate<Object> compare(Field field, String remainingCondition) {
        List<String> numericalOperators = List.of("=", "!=", "<=", ">=", ">", "<", "in ");
        String rightOperand = "";
        String usedOperator = "";
        for (String operator : numericalOperators) {
            if (remainingCondition.startsWith(operator)) {
                rightOperand = remainingCondition.replaceFirst(operator, "");
                usedOperator = operator;
                break;
            }
        }

        if (usedOperator.equals("in ") && rightOperand.startsWith("[")) {
            try {
                return numericalListComparison(field, rightOperand);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage());
            }
        }

        if (isNumber(rightOperand)) {
            try {
                return simpleNumericalComparison(field, usedOperator, rightOperand);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage());
            }
        }
        return t -> true;
    }

    private Predicate<Object> simpleNumericalComparison(Field field, String usedOperator, String operand) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        final Double rightOperand = Double.parseDouble(operand);
        List<Method> methodsCopy = new ArrayList<>(methods);
        return t -> {
            try {
                Double leftOperand = ((Number) invokeAllMethods(t, methodsCopy)).doubleValue();
                switch (usedOperator) {
                    case "=":
                        return leftOperand.equals(rightOperand);
                    case "<=":
                        return leftOperand <= rightOperand;
                    case ">=":
                        return leftOperand >= rightOperand;
                    case ">":
                        return leftOperand > rightOperand;
                    case "<":
                        return leftOperand < rightOperand;
                    case "!=":
                        return !leftOperand.equals(rightOperand);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.info(e.getMessage());
            }
            return true;
        };
    }

    private Predicate<Object> numericalListComparison(final Field field, String rightOperand) throws NoSuchMethodException {
        //list comparison
        String[] numbers = rightOperand.split("\\[")[1].split("]")[0].split(",");

        List<Method> methodsCopy = getMethods(field);
        return t -> Arrays.stream(numbers).map(Double::parseDouble).anyMatch(number -> {
                    try {
                        return ((Number) invokeAllMethods(t, methodsCopy)).doubleValue() == number;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.info(e.getMessage());
                    }
                    return true;
                }
        );

    }

}
