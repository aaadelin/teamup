package com.team.teamup.utils.query.comparator;

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

    public ListCompare(Deque<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    /**
     *
     * @param field class field
     * @param fieldName name of the field as specified in annotation
     * @param remainingCondition string containing operator + operand provided by user
     * @param searchTerms map with strings placeholders
     * @return predicate
     */
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

    /**
     *
     * @param field class field
     * @param fieldName name of the field, will be used in vuture implementations
     * @param operator binary operator
     * @param rightOperator string containing the right operator, should be a list with items separated by comma
     * @param searchTerms map of wuoted items, used if lst contains strings
     * @return predicate
     * @throws NoSuchMethodException if field doesn't have a getter
     */
    @SuppressWarnings("unchecked")
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
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                log.info(e.getMessage());
            }
            //todo throw exception
            return true;
        };
    }

    /**
     * get predicate by comparing the field value to the right operator
     * @param operand object from list to compare to right operator
     * @param field class field, will be used in multiple level search
     * @param rightOperator list of items, should be number, string, simple objects (not lists)
     * @param searchTerms pairs of tag - strings for quoted strings
     * @param fieldMap fields in the class
     * @param attribute name of the attribute in case lists contains objects
     * @return predicate
     * //todo add enums
     */
    private Predicate<Object> getPredicateForListOperand(Object operand, Field field, String rightOperator, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) {

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
