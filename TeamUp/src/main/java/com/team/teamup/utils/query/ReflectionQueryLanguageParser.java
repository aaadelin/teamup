package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class ReflectionQueryLanguageParser<T, I> extends AbstractLanguageParser<T, I> {

    private final Stack<Class<?>> classes;
    private Map<String, Field> fields;
    private final Queue<Method> methods;

    public ReflectionQueryLanguageParser(JpaRepository<T, I> repository) {
        super(repository);
        fields = new HashMap<>();
        classes = new Stack<>();
        methods = new ArrayDeque<>();
    }

    public void setClazz(Class<T> clazz) {
        classes.push(clazz);
        fields = getSearchFields(clazz);
    }

    @Override
    protected Predicate<T> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition, Map<String, String> aliases) {
        methods.clear();
        List<Predicate<T>> orPredicates = new ArrayList<>();
        for (String orCondition : andCondition.split(" or ")) {
            for (Map.Entry<String, Field> attribute : fields.entrySet()) {
                if (orCondition.startsWith(attribute.getKey().toLowerCase())) {
                    orPredicates.add(binaryOperatorParser(orCondition, fields.get(attribute.getKey()), attribute.getKey(), searchTerms));
                }
            }
        }
        if (orPredicates.isEmpty()) {
            return t -> true;
        }
        return orPredicates.stream().reduce(orPredicates.get(0), Predicate::or);
    }

    private Predicate<T> binaryOperatorParser(String orCondition, Field field, String fieldName, Map<Integer, String> searchTerms) {

        String type = field.getType().getSimpleName();
        String remainingCondition = orCondition.replaceFirst(fieldName.toLowerCase(), "").strip();
        switch (type) {
            case "Integer": // < > = != <= >= in
            case "Double":
            case "Float":
            case "int":
            case "double":
            case "float":
                return compareNumerical(field, remainingCondition);
            case "String": // = like !=
                return compareString(field, remainingCondition, searchTerms);
            case "LocalDate":
            case "LocalDateTime": // = >= <= > <
                return compareDates(field, remainingCondition);
            default:
                return compareObjects(field, remainingCondition, searchTerms);
        }

    }

    private Predicate<T> compareObjects(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        Map<String, Field> fieldMap = getSearchFields(field.getType());
        SearchField annotation = field.getAnnotation(SearchField.class);
        String[] attributes = annotation.attributes();
        if(attributes.length == 0){
            //should be one
            throw new NoAttributesSetException();
        }

        if(attributes.length == 1 && fieldMap.containsKey(attributes[0])){
            //compare directly to that key

            final String attribute = attributes[0];
            return pushAttributeToStack(field, remainingCondition, searchTerms, fieldMap, attribute);
        }

        if (attributes.length > 1){
            //compare to all with ||
            List<Predicate<T>> predicates = new ArrayList<>();
            for(String attribute : attributes){
                predicates.add(pushAttributeToStack(field, remainingCondition, searchTerms, fieldMap, attribute));
            }
            return predicates.stream().reduce(predicates.get(0), Predicate::or);
        }
        return t -> true;
    }

    private Predicate<T> pushAttributeToStack(Field field, String remainingCondition, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) {
        Field field1 = fieldMap.get(attribute);

        String methodName = "get" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
        Method method;
        try {
            method = classes.peek().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
            return t -> true;
        }
        methods.add(method);

        classes.push(field.getType());
        Predicate<T> predicate = binaryOperatorParser(attribute + remainingCondition, field1, attribute, searchTerms);
        classes.pop();
        return predicate;
    }

    private Predicate<T> compareDates(Field field, String remainingCondition) {
        List<String> dateOperators = List.of("=", "!=", ">=", "<=", "<", ">");
        for (String operator : dateOperators) {
            if (remainingCondition.startsWith(operator)) {
                LocalDateTime rightOperand = getDateFromRightOperand(remainingCondition.replaceFirst(operator, ""));
                try {
                    return simpleDateOperator(field, operator, rightOperand);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        return t -> true;
    }

    private Predicate<T> simpleDateOperator(Field field, String operator, LocalDateTime rightOperand) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return t -> {
            try {
                LocalDateTime leftOperand;
                if (field.getType().getSimpleName().equals("LocalDate")) {
                    leftOperand = ((LocalDate) invokeAllMethods(t)).atStartOfDay();
                } else {
                    leftOperand = (LocalDateTime) invokeAllMethods(t);
                }

                switch (operator) {
                    case "=":
                        return checkDateEquality(leftOperand, rightOperand);
                    case "!=":
                        return !checkDateEquality(leftOperand, rightOperand);
                    case ">=":
                        return leftOperand.isAfter(rightOperand) || checkDateEquality(leftOperand, rightOperand);
                    case "<=":
                        return leftOperand.isBefore(rightOperand) || checkDateEquality(leftOperand, rightOperand);
                    case "<":
                        return leftOperand.isBefore(rightOperand);
                    case ">":
                        return leftOperand.isAfter(rightOperand);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return true;
            }
        };
    }

    private boolean checkDateEquality(LocalDateTime leftOperand, LocalDateTime rightOperand) {
        if (rightOperand.getHour() == 0 && rightOperand.getMinute() == 0 && rightOperand.getSecond() == 0) {
            //no time provided
            return leftOperand.getYear() == rightOperand.getYear() &&
                    leftOperand.getMonthValue() == rightOperand.getMonthValue() &&
                    leftOperand.getDayOfMonth() == rightOperand.getDayOfMonth();
        }
        return leftOperand.equals(rightOperand);
    }

    private LocalDateTime getDateFromRightOperand(String operator) {
        if (operator.contains(":")) {
            //contains hour
            String[] components = operator.strip().split(" ");
            String date = components[0].strip();
            String time = components[1].strip();
            return LocalDateTime.of(getDateFromString(date), getTimeFromString(time));
        }

        return LocalDateTime.of(getDateFromString(operator), LocalTime.of(0, 0));
    }

    private LocalDate getDateFromString(String date) {
        Integer[] components = Arrays.stream(date.split("-")).map(String::strip).map(Integer::parseInt).toArray(Integer[]::new);
        return LocalDate.of(components[0], components[1], components[2]);
    }

    private LocalTime getTimeFromString(String time) {
        Integer[] components = Arrays.stream(time.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        if (components.length == 2) {
            return LocalTime.of(components[0], components[1]);
        }
        return LocalTime.of(components[0], components[1], components[2]);
    }

    private Predicate<T> compareString(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        List<String> stringOperators = List.of("=", "!=", "like ");
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

    private Predicate<T> simpleStringOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return t -> {
            try {
                switch (operator) {
                    case "=":
                        return ((String) invokeAllMethods(t)).strip().equalsIgnoreCase(rightOperand);
                    case "!=":
                        return !((String) invokeAllMethods(t)).strip().equalsIgnoreCase(rightOperand);
                    case "like ":
                        return ((String) invokeAllMethods(t)).strip().toLowerCase().contains(rightOperand);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return true;
            }
        };
    }

    private String getQuotedRightOperand(String operand, Map<Integer, String> searchTerms) {
        String key = operand.split("\\{")[1].split("}")[0];
        if (isInteger(key)) {
            return searchTerms.get(Integer.parseInt(key)).strip();
        }
        return "";
    }

    private Predicate<T> compareNumerical(Field field, String remainingCondition) {
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

    private Predicate<T> simpleNumericalComparison(Field field, String usedOperator, String operand) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        final Double rightOperand = Double.parseDouble(operand);
        return t -> {
            try {
                Double leftOperand = ((Number) invokeAllMethods(t)).doubleValue();
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

    private Predicate<T> numericalListComparison(final Field field, String rightOperand) throws NoSuchMethodException {
        //list comparison
        String[] numbers = rightOperand.split("\\[")[1].split("]")[0].split(",");
        String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

        Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return t -> Arrays.stream(numbers).map(Double::parseDouble).anyMatch(number -> {
                    try {
                        return ((Number) invokeAllMethods(t)).doubleValue() == number;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.info(e.getMessage());
                    }
                    return true;
                }
        );

    }

    @Override
    Map<String, String> extractAliases(String condition) {
        HashMap<String, String> aliases = new HashMap<>();

        aliases.put(classes.peek().getSimpleName(), classes.peek().getSimpleName().toLowerCase());
        return aliases;
    }

    private Map<String, Field> getSearchFields(Class<?> clazz) {
        Map<String, Field> classFields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            SearchField annotation = field.getAnnotation(SearchField.class);
            if (annotation != null) {
                String name = field.getName();
                if (!annotation.name().equals("")) {
                    name = annotation.name();
                }
                classFields.put(name, field);
            }
        }
        return classFields;
    }

    private Object invokeAllMethods(T t) throws InvocationTargetException, IllegalAccessException {
        Object o = t;
        for(Method method : methods){
            o = method.invoke(o);
        }
        return o;
    }
}
