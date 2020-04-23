package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.comparator.AbstractComparator;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Predicate;

import static com.team.teamup.utils.query.AbstractLanguageParser.isInteger;
import static com.team.teamup.utils.query.ReflectionQueryLanguageParser.getSearchFields;

@Slf4j
public class ListCompare extends AbstractComparator {

    public ListCompare(Stack<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    public Predicate<Object> compare(Field field, String fieldName, String remainingCondition, Map<Integer, String> searchTerms) {
        List<String> listOperators = List.of("=", "in ");
        for (String operator : listOperators) {
            if (remainingCondition.startsWith(operator)) {
                String rightOperator = remainingCondition.replaceFirst(remainingCondition, operator);
                try {
                    return compareLists(field, fieldName, operator, rightOperator, searchTerms);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        return t -> true;
    }

    private Predicate<Object> compareLists(Field field, String fieldName, final String operator, String rightOperator, Map<Integer, String> searchTerms) throws NoSuchMethodException {
        final List<Method> methodsCopy = getMethods(field);
        methods.remove();
        return t -> {
            try {
                Class<?> listType = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                List<Object> leftOperand = (List<Object>) invokeAllMethods(t, methodsCopy);
                Map<String, Field> attributes = getSearchFields(listType);
                String attribute = field.getAnnotation(SearchField.class).attribute();
                if (leftOperand == null) {
                    return true;
                }

                List<Boolean> predicates = new ArrayList<>();
                for (Object operand : leftOperand) {
                    predicates.add(getPredicateForListOperand(operand, field, rightOperator, searchTerms, attributes, attribute).test(operand));
                }
                switch (operator) {
                    case "=":
                        return predicates.stream().reduce(predicates.get(0), Boolean::logicalAnd);
                    case "in ":
                        return predicates.stream().reduce(predicates.get(0), Boolean::logicalOr);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
                log.info(e.getMessage());
            }
            return true;
        };
    }

    private Predicate<Object> getPredicateForListOperand(Object operand, Field field, String rightOperator, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) throws NoSuchMethodException {

        String operandType = operand.getClass().getSimpleName();
        String[] rightHandItems = rightOperator.split("\\[")[1].split("]")[0].split(",");
        switch (operandType) {
            case "Double":
            case "Integer":
                return t -> Arrays.stream(rightHandItems).map(Double::parseDouble).anyMatch(number -> ((Number) t).doubleValue() == number
                );
            case "String":
                return t -> Arrays.stream(rightHandItems).map(String::strip).anyMatch(string -> {
                            String value = string.split("\\{")[1].split("}")[0];
                            if (isInteger(value)) {
                                return t.equals(searchTerms.get(Integer.parseInt(value)));
                            }
                            return true;
                        }
                );
            default:
                return t -> {
                    try {
                        String methodName = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1).toLowerCase();
                        Method mehod = t.getClass().getDeclaredMethod(methodName);
                        return Arrays.stream(rightHandItems).map(String::strip).anyMatch(string -> {
                            String value = string.split("\\{")[1].split("}")[0];
                            if (isInteger(value)) {
                                try {
                                    return ((String)mehod.invoke(t)).equalsIgnoreCase(searchTerms.get(Integer.parseInt(value)));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    log.info(e.getMessage());
                                    return true;
                                }
                            }
                            return true;
                        });
                    } catch (NoSuchMethodException e) {
                        log.info(e.getMessage());
                        return true;
                    }
                };
        }
    }

}
