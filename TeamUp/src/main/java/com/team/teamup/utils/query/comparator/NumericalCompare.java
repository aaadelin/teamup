package com.team.teamup.utils.query.comparator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static com.team.teamup.utils.query.AbstractLanguageParser.isNumber;

@Slf4j
public class NumericalCompare extends AbstractComparator {

    /**
     *
     * @param classes stack of classes used for multimple-level search
     * @param methods methods to apply to obtain the field
     */
    public NumericalCompare(Deque<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    /**
     * method to compare
     * @param field must be a numerical field in class
     * @param remainingCondition string with the remaining condition (e.g.  =1 | in [1, 2, 3])
     * @return predicate
     */
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


        // if is a list, parse the list items
        if (usedOperator.equals("in ") && rightOperand.startsWith("[")) {
            try {
                return numericalListComparison(field, rightOperand);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage());
            }
        }

        // if is not a list, and it's a number, compare the operands
        if (isNumber(rightOperand)) {
            try {
                return simpleNumericalComparison(field, usedOperator, rightOperand);
            } catch (NoSuchMethodException e) {
                log.info(e.getMessage());
            }
        }

        // default case todo make this an exception
        return t -> true;
    }

    /**
     * compares two numerical values casting them to double
     * @param field field in class to be checked
     * @param usedOperator operator between numbers
     * @param operand right operand
     * @return predicate
     * @throws NoSuchMethodException if ca't find the getter for the field in the class
     */
    private Predicate<Object> simpleNumericalComparison(Field field, String usedOperator, String operand) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        assert classes.peek() != null;
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

    /**
     * compare left operand, a number with right operand, a list of numbers
     * @param field class field
     * @param rightOperand list of numbers separated by comma
     * @return predicate
     * @throws NoSuchMethodException if the field doesn't have a getter
     * @throws NumberFormatException if the right operand contains an non-numeric item
     */
    private Predicate<Object> numericalListComparison(final Field field, String rightOperand) throws NoSuchMethodException {
        //list comparison
        String[] numbers = rightOperand.split("\\[")[1].split("]")[0].split(",");

        List<Method> methodsCopy = getMethods(field);
        // parse right hand items to doubles
        return t -> Arrays.stream(numbers).map(Double::parseDouble).anyMatch(number -> {
                    try {
                        // cast left hand operand to double and compare
                        return ((Number) invokeAllMethods(t, methodsCopy)).doubleValue() == number;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.info(e.getMessage());
                    }
                    //todo throw exception
                    return true;
                }
        );

    }

}
