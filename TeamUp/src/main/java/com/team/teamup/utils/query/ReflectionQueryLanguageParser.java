package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class ReflectionQueryLanguageParser<T, I> extends AbstractLanguageParser<T, I> {

    private final Stack<Class<?>> classes;
    private Map<String, Field> fields;
    private final Queue<Method> methods;
    private T t;
    //todo use lists

    public ReflectionQueryLanguageParser(JpaRepository<T, I> repository) {
        super(repository);
        fields = new HashMap<>();
        classes = new Stack<>();
        methods = new ArrayDeque<>();
    }

    public void setClazz(Class<T> clazz) {
        classes.push(clazz);
    }

    @Override
    protected Predicate<T> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition) {
        fields = getSearchFields(classes.peek());
        List<Predicate<T>> orPredicates = new ArrayList<>();
        for (String orCondition : andCondition.split(" or ")) {

            for (Map.Entry<String, Field> attribute : fields.entrySet()) {
                if (orCondition.startsWith(attribute.getKey().toLowerCase())) {
                    methods.clear();
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
        if (field.getType().isEnum()) {
            type = "Enum";
        }
        String remainingCondition = orCondition.replaceFirst(fieldName.toLowerCase(), "").strip();
        switch (type) {
            case "Integer": // < > = != <= >= in
            case "Double":
            case "Float":
            case "int":
            case "double":
            case "float":
                return compareNumerical(field, remainingCondition);
            case "String": // = like != in
                return compareString(field, remainingCondition, searchTerms);
            case "LocalDate":
            case "LocalDateTime": // = >= <= > <
                return compareDates(field, remainingCondition);
            case "Enum": // = != in
                return compareEnums(field, remainingCondition);
            case "List": // = in/contains
                return compareLists(field, fieldName, remainingCondition, searchTerms);
            default:
                return compareObjects(field, remainingCondition, searchTerms);
        }

    }

    private Predicate<T> compareLists(Field field, String fieldName, String remainingCondition, Map<Integer, String> searchTerms) {
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

    private Predicate<T> compareLists(Field field, String fieldName, final String operator, String rightOperator, Map<Integer, String> searchTerms) throws NoSuchMethodException {
        final List<Method> methodsCopy = getMethods(field);
        methods.remove();
        return t -> {
            try {
                Class<?> listType = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                List leftOperand = (List) invokeAllMethods(t, methodsCopy);
                Map<String, Field> attributes = getSearchFields(listType);
                String attribute = field.getAnnotation(SearchField.class).attribute();
                if (leftOperand == null) {
                    return true;
                }

                List<Predicate<T>> predicates = new ArrayList<>();
                for (Object operand : leftOperand) {
                    predicates.add(pushAttributeToStack(field, rightOperator, searchTerms, attributes, attribute)); //todo use operand
                }
                switch (operator) {
                    case "=":
                        return predicates.stream().reduce(predicates.get(0), Predicate::and).test(t);
                    case "in ":
                        return predicates.stream().reduce(predicates.get(0), Predicate::or).test(t);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        };
    }


    private Predicate<T> compareEnums(Field field, String remainingCondition) {
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

    private Predicate<T> enumOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
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

    private Predicate<T> compareObjects(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        Map<String, Field> fieldMap = getSearchFields(field.getType());
        SearchField annotation = field.getAnnotation(SearchField.class);

        if (remainingCondition.startsWith(".")) {
            return pushAttributeToStack(field, remainingCondition.substring(1), searchTerms, fieldMap, findNextAttribute(remainingCondition.substring(1)));
        }

        String attribute = annotation.attribute();
        if (attribute.equalsIgnoreCase("")) {
            //should be one
            throw new NoAttributesSetException();
        }

        if (fieldMap.containsKey(attribute)) {
            //compare directly to that key
            return pushAttributeToStack(field, attribute + remainingCondition, searchTerms, fieldMap, attribute);
        }

        return t -> true;
    }

    private String findNextAttribute(String substring) {
        //separator: spatiu . = < > !
        List<String> separators = List.of(" ", ".", "<", ">", "=", "!");

        int minIndex = substring.length();
        String minSeparator = "";
        for (String separator : separators) {
            int idx = substring.indexOf(separator);
            if (idx < minIndex && idx != -1) {
                minIndex = idx;
                minSeparator = separator;
            }
        }
        if (minSeparator.equals(".")) {
            minSeparator = "\\.";
        }
        return substring.split(minSeparator)[0];
    }

    private Predicate<T> pushAttributeToStack(Field field, String remainingCondition, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) {
        Field field1 = fieldMap.get(attribute);

        String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method method;
        try {
            method = classes.peek().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
            return t -> true;
        }
        methods.add(method);

        Class<?> type;
        if(field.getType().getSimpleName().equalsIgnoreCase("list")){
            try {
                type = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                return t-> true;
            }
        }else{
            type = field.getType();
        }

        classes.push(type);
        Predicate<T> predicate = binaryOperatorParser(remainingCondition, field1, attribute, searchTerms);
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
        ArrayList<Method> methodsCopy = getMethods(field);
        return t -> {
            try {
                LocalDateTime leftOperand;
                if (field.getType().getSimpleName().equals("LocalDate")) {
                    leftOperand = ((LocalDate) invokeAllMethods(t, methodsCopy)).atStartOfDay();
                } else {
                    leftOperand = (LocalDateTime) invokeAllMethods(t, methodsCopy);
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

    private ArrayList<Method> getMethods(Field field) throws NoSuchMethodException {
        final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        final Method method = classes.peek().getDeclaredMethod(methodName);
        methods.add(method);
        return new ArrayList<>(this.methods);
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

    private Predicate<T> simpleStringOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
        ArrayList<Method> methodsCopy = getMethods(field);
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

    private String getQuotedRightOperand(String operand, Map<Integer, String> searchTerms) {
        List<String> quotedStrings = new ArrayList<>();
        for(String item : operand.split(",")) {
            String key = item.split("\\{")[1].split("}")[0];
            if (isInteger(key)) {
                quotedStrings.add(searchTerms.get(Integer.parseInt(key)).strip());
            }
        }
        return String.join(",", quotedStrings);
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

    private Predicate<T> numericalListComparison(final Field field, String rightOperand) throws NoSuchMethodException {
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

    public static Map<String, Field> getSearchFields(Class<?> clazz) {
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

    private Object invokeAllMethods(T t, List<Method> methods) throws InvocationTargetException, IllegalAccessException {
        Object o = t;
        for (Method method : methods) {
            o = method.invoke(o);
        }
        return o;
    }
}
