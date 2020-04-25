package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchField;
import com.team.teamup.utils.query.comparator.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Class to parse query using reflection for given type T
 * @param <T> Class type
 * @param <I> ID Type for class
 */
@Slf4j
public class ReflectionQueryLanguageParser<T, I> extends AbstractLanguageParser<T, I> {

    private final Deque<Class<?>> classes;
    private Map<String, Field> fields;
    private final Queue<Method> methods;
    private final NumericalCompare numericalCompare;
    private final DateCompare dateCompare;
    private final StringCompare stringCompare;
    private final EnumCompare enumCompare;
    private final ObjectsCompare objectsCompare;
    private final ListCompare listCompare;

    public ReflectionQueryLanguageParser(JpaRepository<T, I> repository) {
        super(repository);
        fields = new HashMap<>();
        classes = new ArrayDeque<>();
        methods = new ArrayDeque<>();

        stringCompare = new StringCompare(classes, methods);
        numericalCompare = new NumericalCompare(classes, methods);
        dateCompare = new DateCompare(classes, methods);
        enumCompare = new EnumCompare(classes, methods);
        objectsCompare = new ObjectsCompare(classes, methods);
        listCompare = new ListCompare(classes, methods);
        objectsCompare.setRecursiveMethod(parameters -> binaryOperatorParser(parameters[0], parameters[1], parameters[2], parameters[3]));
    }

    /**
     * Method used for object recursivity
     * @param parameter string representing the condition
     * @param parameter1 field representing the field in class
     * @param parameter2 fieldName, or alias
     * @param parameter3 searchTerms map containing quoted values from condition
     * @return predicate
     */
    @SuppressWarnings(value = "unchecked")
    private Predicate<Object> binaryOperatorParser(Object parameter, Object parameter1, Object parameter2, Object parameter3) {
        String orCondition = (String) parameter;
        Field field = (Field) parameter1;
        String fieldName = (String) parameter2;
        Map<Integer, String> searchTerms = (Map<Integer, String>) parameter3;
        return binaryOperatorParser(orCondition, field, fieldName, searchTerms);
    }

    /**
     * adding the starting class
     * @param clazz starting class for parameter type
     */
    public void setClazz(Class<T> clazz) {
        classes.push(clazz);
    }

    /**
     * Given a string containing predicates separated with "or", return a predicate with the reduced conditions
     * @param searchTerms map containing quoted parts from conditions
     * @param andCondition string containing multimple or-separated conditions
     * @return predicate
     */
    @Override
    protected Predicate<Object> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition) {
        assert classes.peek() != null;
        fields = getSearchFields(classes.peek());
        List<Predicate<Object>> orPredicates = new ArrayList<>();
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

    /**
     *
     * @param orCondition simple condition (E.g. status="open")
     * @param field class field to compare to
     * @param fieldName name of the field, in case of objects it means the name of the field in that object
     * @param searchTerms map with quoted parts of the query
     * @return predicate
     */
    private Predicate<Object> binaryOperatorParser(String orCondition, Field field, String fieldName, Map<Integer, String> searchTerms) {
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
                return numericalCompare.compare(field, remainingCondition);
            case "String": // = like != in
                return stringCompare.compare(field, remainingCondition, searchTerms);
            case "LocalDate":
            case "LocalDateTime": // = >= <= > <
                return dateCompare.compare(field, remainingCondition);
            case "Enum": // = != in
                return enumCompare.compare(field, remainingCondition);
            case "List": // = in/contains
                return listCompare.compare(field, fieldName, remainingCondition, searchTerms);
            default:
                return objectsCompare.compare(field, remainingCondition, searchTerms);
        }

    }

    /**
     * get available fields to search in a class
     * @param clazz class to search in
     * @return map with name of field and the field in the clazz
     */
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
}
