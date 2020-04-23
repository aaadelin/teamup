package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.NoAttributeException;
import com.team.teamup.utils.query.RecursivePredicate;
import com.team.teamup.utils.query.comparator.AbstractComparator;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Predicate;

import static com.team.teamup.utils.query.ReflectionQueryLanguageParser.getSearchFields;

@Slf4j
public class ObjectsCompare extends AbstractComparator {

    public ObjectsCompare(Stack<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    private RecursivePredicate recursiveMethod;
    public void setRecursiveMethod(RecursivePredicate method){
        this.recursiveMethod = method;
    }

    public Predicate<Object> compare(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        Map<String, Field> fieldMap = getSearchFields(field.getType());
        SearchField annotation = field.getAnnotation(SearchField.class);

        if (remainingCondition.startsWith(".")) {
            return pushAttributeToStack(field, remainingCondition.substring(1), searchTerms, fieldMap, findNextAttribute(remainingCondition.substring(1)));
        }

        String attribute = annotation.attribute();
        if (attribute.equalsIgnoreCase("")) {
            //should be one
            throw new NoAttributeException();
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

    private Predicate<Object> pushAttributeToStack(Field field, String remainingCondition, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) {
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
        if (field.getType().getSimpleName().equalsIgnoreCase("list")) {
            try {
                type = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                return t -> true;
            }
        } else {
            type = field.getType();
        }

        classes.push(type);
        Predicate<Object> predicate = recursiveMethod.execute(remainingCondition, field1, attribute, searchTerms);
        classes.pop();
        return predicate;
    }

}
