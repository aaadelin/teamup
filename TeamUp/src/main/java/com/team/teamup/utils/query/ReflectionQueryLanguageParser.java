package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchField;
import com.team.teamup.utils.query.comparator.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class ReflectionQueryLanguageParser<T, I> extends AbstractLanguageParser<T, I> {

    private final Stack<Class<?>> classes;
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
        classes = new Stack<>();
        methods = new ArrayDeque<>();

        stringCompare = new StringCompare(classes, methods);
        numericalCompare = new NumericalCompare(classes, methods);
        dateCompare = new DateCompare(classes, methods);
        enumCompare = new EnumCompare(classes, methods);
        objectsCompare = new ObjectsCompare(classes, methods);
        listCompare = new ListCompare(classes, methods);
        objectsCompare.setRecursiveMethod(parameters -> binaryOperatorParser(parameters[0], parameters[1], parameters[2], parameters[3]));
    }

    private Predicate<Object> binaryOperatorParser(Object parameter, Object parameter1, Object parameter2, Object parameter3) {
        String orCondition = (String) parameter;
        Field field = (Field) parameter1;
        String fieldName = (String) parameter2;
        Map<Integer, String> searchTerms = (Map<Integer, String>) parameter3;
        return binaryOperatorParser(orCondition, field, fieldName, searchTerms);
    }

    public void setClazz(Class<T> clazz) {
        classes.push(clazz);
    }

    @Override
    protected Predicate<T> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition) {
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
        return (Predicate<T>) orPredicates.stream().reduce(orPredicates.get(0), Predicate::or);
    }

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
