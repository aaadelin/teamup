package com.team.teamup.utils.query.comparator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.team.teamup.utils.query.AbstractLanguageParser.isInteger;

@Slf4j
public class StringCompare extends AbstractComparator {

    public StringCompare(Deque<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    /**
     *
     * @param field class field to compare
     * @param remainingCondition string containing operator + right operand
     * @param searchTerms map of placeholders and strings
     * @return predicate
     */
    public Predicate<Object> compare(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        List<String> stringOperators = List.of("=", "!=", "like ", "in ");
        for (String operator : stringOperators) {
            if (remainingCondition.startsWith(operator)) {
                String rightOperand = getQuotedRightOperand(remainingCondition.replaceFirst(operator, ""), searchTerms);
                try {
                    return simpleStringOperator(field, operator, rightOperand);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        return t -> true;
    }

    /**
     *
     * @param field class field to compare, should be string
     * @param operator compare operator (=, <=, ...)
     * @param rightOperand string containing a string or a list of strings separated by comma
     * @return predicate
     * @throws NoSuchMethodException if field doesn't have a getter associated
     */
    private Predicate<Object> simpleStringOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
        List<Method> methodsCopy = getMethods(field);
        return t -> {
            try {
                switch (operator) {
                    case "=":
                        return ((String) invokeAllMethods(t, methodsCopy)).strip().equalsIgnoreCase(rightOperand);
                    case "!=":
                        return !((String) invokeAllMethods(t, methodsCopy)).strip().equalsIgnoreCase(rightOperand);
                    case "like ":
                        return ((String) invokeAllMethods(t, methodsCopy)).strip().toLowerCase().contains(rightOperand);
                    case "in ":
                        String objectAttribute = ((String) invokeAllMethods(t, methodsCopy)).strip().toLowerCase();
                        List<String> listToAppearIn = Arrays.stream(rightOperand.replace("[", "").replace("]", "").split(",")).map(String::strip).collect(Collectors.toList());
                        return listToAppearIn.contains(objectAttribute);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return true;
            }
        };
    }

    /**
     * convert list of string tags to actual strings
     * @param operand string of placeholders
     * @param searchTerms map containing the placeholders as keys and strings as values
     * @return list of strings separated by comma
     */
    private String getQuotedRightOperand(String operand, Map<Integer, String> searchTerms) {
        List<String> quotedStrings = new ArrayList<>();
        for (String item : operand.split(",")) {
            String key = item.split("\\{")[1].split("}")[0];
            if (isInteger(key)) {
                quotedStrings.add(searchTerms.get(Integer.parseInt(key)).strip());
            }
        }
        return String.join(",", quotedStrings);
    }
}
